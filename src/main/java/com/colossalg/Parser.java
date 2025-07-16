package com.colossalg;

import com.colossalg.expression.*;
import com.colossalg.statement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {

    private static class ParserException extends Exception {}

    @FunctionalInterface
    private interface GetNextExpression {
        Expression get() throws ParserException;
    }

    @FunctionalInterface
    private interface CreateLhsRhsOpExpression {
        @SuppressWarnings("unused") // False alarm I believe, these are all forwarded to Expression subclass constructors.
        Expression get(
                String file,
                int line,
                Token operator,
                Expression lhsSubExpression,
                Expression rhsSubExpression);
    }

    public Parser(ErrorReporter errorReporter, List<Token> tokens) {
        _errorReporter = errorReporter;
        _tokens = tokens;
    }

    public List<Statement> parse() {
        final var statements = new ArrayList<Statement>();
        while (isNotAtEnd()) {
            statements.add(parseStatement());
        }
        return statements;
    }

    private Statement parseStatement() {
        try {
            if (match(TokenType.CLASS)) {
                return parseClassDeclaration();
            } else if (match(TokenType.FUN)) {
                return parseFunDeclaration();
            } else if (match(TokenType.VAR)) {
                return parseVarDeclaration();
            } else {
                return parseNonDeclarationStatement();
            }
        } catch (ParserException ex) {
            synchronize();
            return null;
        }
    }

    private void synchronize() {
        _index++;
        while (isNotAtEnd()) {
            if (match(TokenType.SEMICOLON)) {
                _index++;
                break;
            } else if (match(
                    TokenType.CLASS,
                    TokenType.FUN,
                    TokenType.VAR,
                    TokenType.IF,
                    TokenType.WHILE,
                    TokenType.FOR,
                    TokenType.PRINT,
                    TokenType.RETURN
            )) {
                break;
            } else {
                _index++;
            }
        }
    }

    private ClassDeclaration parseClassDeclaration() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.CLASS);
        final var identifier = peek();
        consume(TokenType.IDENTIFIER);

        Token superClass = null;
        if (match(TokenType.LESS_THAN)) {
            consume(TokenType.LESS_THAN);
            superClass = peek();
            consume(TokenType.IDENTIFIER);
        }

        consume(TokenType.LFT_BRACE);
        final var methods = new ArrayList<FunDeclaration>();
        while (isNotAtEnd() && !match(TokenType.RGT_BRACE)) {
            methods.add(parseFunDeclaration());
        }
        consume(TokenType.RGT_BRACE);

        return new ClassDeclaration(file, line, identifier, superClass, methods);
    }

    private FunDeclaration parseFunDeclaration() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.FUN);
        final var identifier = peek();
        consume(TokenType.IDENTIFIER);

        consume(TokenType.LFT_PARENTHESIS);
        final var parameters = new ArrayList<Token>();
        while (isNotAtEnd() && !match(TokenType.RGT_PARENTHESIS)) {
            parameters.add(peek());
            consume(TokenType.IDENTIFIER);
            if (match(TokenType.COMMA)) {
                consume(TokenType.COMMA);
            }
        }
        consume(TokenType.RGT_PARENTHESIS);

        consume(TokenType.LFT_BRACE);
        final var statements = new ArrayList<Statement>();
        while (isNotAtEnd() && !match(TokenType.RGT_BRACE)) {
            final var statement = parseStatement();
            if (statement != null) {
                statements.add(statement);
            }
        }
        consume(TokenType.RGT_BRACE);

        return new FunDeclaration(file, line, identifier, parameters, statements);
    }

    private VarDeclaration parseVarDeclaration() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.VAR);
        final var identifier = peek();
        consume(TokenType.IDENTIFIER);
        consume(TokenType.EQUAL);
        final var expression = parseExpression();
        consume(TokenType.SEMICOLON);

        return new VarDeclaration(file, line, identifier, expression);
    }

    private Statement parseNonDeclarationStatement() throws ParserException {
        if (match(TokenType.IF)) {
            return parseIfElseStatement();
        } else if (match(TokenType.WHILE)) {
            return parseWhileStatement();
        } else if (match(TokenType.FOR)) {
            return parseForStatement();
        } else if (match(TokenType.LFT_BRACE)) {
            return parseBlockStatement();
        } else if (match(TokenType.RETURN)) {
            return parseReturnStatement();
        } else if (match(TokenType.PRINT)) {
            return parsePrintStatement();
        } else {
            return parseExpressionStatement();
        }
    }

    private IfElseStatement parseIfElseStatement() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.IF);
        consume(TokenType.LFT_PARENTHESIS);
        final var condition = parseExpression();
        consume(TokenType.RGT_PARENTHESIS);
        Statement thenStatement = parseNonDeclarationStatement();
        Statement elseStatement = null;
        if (match(TokenType.ELSE)) {
            consume(TokenType.ELSE);
            elseStatement = parseNonDeclarationStatement();
        }

        return new IfElseStatement(file, line, condition, thenStatement, elseStatement);
    }

    private WhileStatement parseWhileStatement() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.WHILE);
        consume(TokenType.LFT_PARENTHESIS);
        final var condition = parseExpression();
        consume(TokenType.RGT_PARENTHESIS);
        final var subStatement = parseNonDeclarationStatement();

        return new WhileStatement(file, line, condition, subStatement);
    }

    private ForStatement parseForStatement() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.FOR);
        consume(TokenType.LFT_PARENTHESIS);

        // initializer is any of:
        //  - A statement implicitly ending in ';' (variable declaration or expression statement)
        //  - Empty, in which case it is just ';'
        Statement initializer = null;
        if (!match(TokenType.SEMICOLON)) {
            initializer = match(TokenType.VAR)
                    ? parseVarDeclaration()
                    : parseExpressionStatement(); // Includes trailing semicolon
        } else {
            consume(TokenType.SEMICOLON);
        }

        // condition is an optional expression which MUST be followed by a ';'
        Expression condition = null;
        if (!match(TokenType.SEMICOLON)) {
            condition = parseExpression();
            consume(TokenType.SEMICOLON);
        } else {
            consume(TokenType.SEMICOLON);
        }

        // increment is an optional expression which MUST be followed by a ')'
        Expression increment = null;
        if (!match(TokenType.RGT_PARENTHESIS)) {
            increment = parseExpression();
            consume(TokenType.RGT_PARENTHESIS);
        } else {
            consume(TokenType.RGT_PARENTHESIS);
        }

        final var subStatement = parseNonDeclarationStatement();

        return new ForStatement(file, line, initializer, condition, increment, subStatement);
    }

    private BlockStatement parseBlockStatement() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.LFT_BRACE);
        final var subStatements = new ArrayList<Statement>();
        while (isNotAtEnd() && !match(TokenType.RGT_BRACE)) {
            final var subStatement = parseStatement();
            if (subStatement != null) {
                subStatements.add(subStatement);
            }
        }
        consume(TokenType.RGT_BRACE);

        return new BlockStatement(file, line, subStatements);
    }

    private ReturnStatement parseReturnStatement() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.RETURN);
        Expression subExpression = null;
        if (!match(TokenType.SEMICOLON)) {
            subExpression = parseExpression();
        }
        consume(TokenType.SEMICOLON);

        return new ReturnStatement(file, line, subExpression);
    }

    private PrintStatement parsePrintStatement() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.PRINT);
        final var subExpression = parseExpression();
        consume(TokenType.SEMICOLON);

        return new PrintStatement(file, line, subExpression);
    }

    private ExpressionStatement parseExpressionStatement() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        final var subExpression = parseExpression();
        consume(TokenType.SEMICOLON);

        return new ExpressionStatement(file, line, subExpression);
    }

    private Expression parseExpression() throws ParserException {
        return parseAssignment();
    }

    private Expression parseAssignment() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        var result = parseLogicalOp();

        if (match(TokenType.EQUAL)) {
            consume(TokenType.EQUAL);
            if (!(result instanceof VarExpression) && !(result instanceof DotExpression)) {
                throw panic(String.format(
                        "Invalid attempt to assign to LHS token with type %s.",
                        peek().getType().name()));
            }
            result = new VarAssignment(file, line, result, parseExpression());
        }

        return result;
    }

    private Expression parseLogicalOp() throws ParserException {
        return parseLogicalOpChain(
                this::parseBinaryOpEqualityComparison,
                TokenType.AND,
                TokenType.OR);
    }

    private Expression parseBinaryOpEqualityComparison() throws ParserException {
        return parseBinaryOpChain(
                this::parseBinaryOpInequalityComparison,
                TokenType.EQUAL_EQUAL,
                TokenType.BANGS_EQUAL);
    }

    private Expression parseBinaryOpInequalityComparison() throws ParserException {
        return parseBinaryOpChain(
                this::parseBinaryOpAddOrSub,
                TokenType.LESS_THAN,
                TokenType.LESS_THAN_OR_EQUAL,
                TokenType.MORE_THAN,
                TokenType.MORE_THAN_OR_EQUAL);
    }

    private Expression parseBinaryOpAddOrSub() throws ParserException {
        return parseBinaryOpChain(
                this::parseBinaryOpMulOrDiv,
                TokenType.ADD,
                TokenType.SUB);
    }

    private Expression parseBinaryOpMulOrDiv() throws ParserException {
        return parseBinaryOpChain(
                this::parseUnaryOpChain,
                TokenType.MUL,
                TokenType.DIV);
    }

    private Expression parseLogicalOpChain(
            GetNextExpression getNextExpr,
            TokenType... tokenTypes
    ) throws ParserException {
        return parseLhsRhsOpChain(getNextExpr, LogicalExpression::new, tokenTypes);
    }

    private Expression parseBinaryOpChain(
            GetNextExpression getNextExpr,
            TokenType... tokenTypes
    ) throws ParserException {
        return parseLhsRhsOpChain(getNextExpr, BinaryExpression::new, tokenTypes);
    }

    private Expression parseLhsRhsOpChain(
            GetNextExpression getNextExpr,
            CreateLhsRhsOpExpression createLhsRhsOpExpr,
            TokenType... tokenTypes
    ) throws ParserException {
        var result = getNextExpr.get();
        while (match(tokenTypes)) {
            final var operator = peek();
            consume(tokenTypes);
            final var lhsSubExpression = result;
            final var rhsSubExpression = getNextExpr.get();
            result = createLhsRhsOpExpr.get(
                    operator.getFile(),
                    operator.getLine(),
                    operator,
                    lhsSubExpression,
                    rhsSubExpression);
        }
        return result;
    }

    private Expression parseUnaryOpChain() throws ParserException {
        final var unaryOpTokenTypes = new TokenType[] { TokenType.BANGS, TokenType.SUB };

        final var precedingOps = new Stack<Token>();
        while (match(unaryOpTokenTypes)) {
            precedingOps.push(peek());
            consume(unaryOpTokenTypes);
        }

        var result = parseAtomic();
        while (!precedingOps.isEmpty()) {
            final var precedingOp = precedingOps.pop();
            result = new UnaryExpression(
                    precedingOp.getFile(),
                    precedingOp.getLine(),
                    precedingOp,
                    result);
        }

        return result;
    }

    private Expression parseAtomic() throws ParserException {
        final var literalTokenTypes = new TokenType[]{
                TokenType.STRING,
                TokenType.NUMBER,
                TokenType.TRUE,
                TokenType.FALSE,
                TokenType.NIL
        };

        if (match(TokenType.NEW, TokenType.LFT_PARENTHESIS, TokenType.IDENTIFIER)) {
            return parseDotAndFunInvocationChain();
        } else if (match(literalTokenTypes)) {
            final var result = new LiteralExpression(
                    peek().getFile(),
                    peek().getLine(),
                    peek());
            consume(literalTokenTypes);
            return result;
        }

        throw panic(String.format(
                "Token type %s is not a valid start for the production 'Atomic'.",
                peek().getType().name()));
    }

    private Expression parseDotAndFunInvocationChain() throws ParserException {
        var result = parseInvokable();
        while (match(TokenType.DOT, TokenType.LFT_PARENTHESIS)) {
            final var file = peek().getFile();
            final var line = peek().getLine();
            if (match(TokenType.DOT)) {
                consume(TokenType.DOT);
                final var lhsExpression = result;
                final var rhsIdentifier = peek();
                consume(TokenType.IDENTIFIER);
                result = new DotExpression(file, line, lhsExpression, rhsIdentifier);
            } else {
                final var subExpression = result;
                final var arguments = parseArgumentList();
                result = new FunInvocation(file, line, subExpression, arguments);
            }
        }
        return result;
    }

    private List<Expression> parseArgumentList() throws ParserException {
        consume(TokenType.LFT_PARENTHESIS);
        final var arguments = new ArrayList<Expression>();
        while (isNotAtEnd() && !match(TokenType.RGT_PARENTHESIS)) {
            arguments.add(parseExpression());
            if (match(TokenType.COMMA)) {
                consume(TokenType.COMMA);
            }
        }
        consume(TokenType.RGT_PARENTHESIS);
        return arguments;
    }

    private Expression parseInvokable() throws ParserException {
        if (match(TokenType.NEW)) {
            return parseNewInvocation();
        } else if (match(TokenType.LFT_PARENTHESIS)) {
            return parseGrouping();
        } else {
            return parseIdentifier();
        }
    }

    private Expression parseNewInvocation() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.NEW);
        final var identifier = peek();
        consume(TokenType.IDENTIFIER);
        final var arguments = parseArgumentList();

        return new NewInvocation(file, line, identifier, arguments);
    }

    private Expression parseGrouping() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.LFT_PARENTHESIS);
        final var subExpression = parseExpression();
        consume(TokenType.RGT_PARENTHESIS);

        return new GroupingExpression(file, line, subExpression);
    }

    private Expression parseIdentifier() throws ParserException {
        final var file = peek().getFile();
        final var line = peek().getLine();

        final var identifier = peek();
        consume(TokenType.IDENTIFIER);

        return new VarExpression(file, line, identifier);
    }

    private void consume(TokenType... tokenTypes) throws ParserException {
        if (!match(tokenTypes)) {
            final var tokenTypesStringBuilder = new StringBuilder();
            for (final var tokenType : tokenTypes) {
                tokenTypesStringBuilder.append(tokenType.name());
                tokenTypesStringBuilder.append(", ");
            }
            throw panic(String.format(
                    "Expected token type [%s], but found token type %s.",
                    tokenTypesStringBuilder,
                    peek().getType().name()));
        }
        _index++;
    }

    private boolean match(TokenType... tokenTypes) {
        for (final var tokenType : tokenTypes) {
            if (peek().getType() == tokenType) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotAtEnd() {
        return peek().getType() != TokenType.EOF;
    }

    private ParserException panic(String what) {
        _errorReporter.report(new JocksError("Parser", peek().getFile(), peek().getLine(), what));
        return new ParserException(); // To unwind call stack back to synchronization point, to resume parsing
    }

    private Token peek() {
        return _tokens.get(_index);
    }

    private final ErrorReporter _errorReporter;
    private final List<Token> _tokens;
    private int _index = 0;
}
