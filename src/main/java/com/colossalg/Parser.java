package com.colossalg;

import com.colossalg.expression.*;
import com.colossalg.statement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {

    @FunctionalInterface
    private interface GetNextExpression {
        Expression get();
    }

    @FunctionalInterface
    private interface CreateLhsRhsOpExpression {
        @SuppressWarnings("unused") // False alarm I believe, these are all forwarded to Expression subclass constructors.
        Expression get(
                Token operator,
                Expression lhsSubExpression,
                Expression rhsSubExpression);
    }

    public Parser(List<Token> tokens) {
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
        if (match(TokenType.CLASS)) {
            return parseClassDeclaration();
        } else if (match(TokenType.FUN)) {
            return parseFunDeclaration();
        } else if (match(TokenType.VAR)) {
            return parseVarDeclaration();
        } else {
            return parseNonDeclarationStatement();
        }
    }

    private ClassDeclaration parseClassDeclaration() {
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

        return new ClassDeclaration(identifier, superClass, methods);
    }

    private FunDeclaration parseFunDeclaration() {
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
            statements.add(parseStatement());
        }
        consume(TokenType.RGT_BRACE);

        return new FunDeclaration(identifier, parameters, statements);
    }

    private VarDeclaration parseVarDeclaration() {
        consume(TokenType.VAR);
        final var identifier = peek();
        consume(TokenType.IDENTIFIER);
        consume(TokenType.EQUAL);
        final var expression = parseExpression();
        consume(TokenType.SEMICOLON);

        return new VarDeclaration(identifier, expression);
    }

    private Statement parseNonDeclarationStatement() {
        if (match(TokenType.IF)) {
            return parseIfElseStatement();
        } else if (match(TokenType.WHILE)) {
            return parseWhileStatement();
        } else if (match(TokenType.FOR)) {
            return parseForStatement();
        } else if (match(TokenType.TRY)) {
            return parseTryCatchStatement();
        } else if (match(TokenType.THROW)) {
            return parseThrowStatement();
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

    private IfElseStatement parseIfElseStatement() {
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

        return new IfElseStatement(condition, thenStatement, elseStatement);
    }

    private WhileStatement parseWhileStatement() {
        consume(TokenType.WHILE);
        consume(TokenType.LFT_PARENTHESIS);
        final var condition = parseExpression();
        consume(TokenType.RGT_PARENTHESIS);
        final var subStatement = parseNonDeclarationStatement();

        return new WhileStatement(condition, subStatement);
    }

    private ForStatement parseForStatement() {
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

        return new ForStatement(initializer, condition, increment, subStatement);
    }

    private TryCatchStatement parseTryCatchStatement() {
        consume(TokenType.TRY);
        final var tryStatement = parseNonDeclarationStatement();
        consume(TokenType.CATCH);
        consume(TokenType.LFT_PARENTHESIS);
        final var exceptionIdentifier = peek();
        consume(TokenType.IDENTIFIER);
        consume(TokenType.RGT_PARENTHESIS);
        final var catchStatement = parseNonDeclarationStatement();

        return new TryCatchStatement(
                tryStatement,
                catchStatement,
                exceptionIdentifier
        );
    }

    private ThrowStatement parseThrowStatement() {
        consume(TokenType.THROW);
        final var subExpression = parseExpression();
        consume(TokenType.SEMICOLON);

        return new ThrowStatement(subExpression);
    }

    private BlockStatement parseBlockStatement() {
        consume(TokenType.LFT_BRACE);
        final var subStatements = new ArrayList<Statement>();
        while (isNotAtEnd() && !match(TokenType.RGT_BRACE)) {
            subStatements.add(parseStatement());
        }
        consume(TokenType.RGT_BRACE);

        return new BlockStatement(subStatements);
    }

    private ReturnStatement parseReturnStatement() {
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

    private PrintStatement parsePrintStatement() {
        consume(TokenType.PRINT);
        final var subExpression = parseExpression();
        consume(TokenType.SEMICOLON);

        return new PrintStatement(subExpression);
    }

    private ExpressionStatement parseExpressionStatement() {
        final var subExpression = parseExpression();
        consume(TokenType.SEMICOLON);

        return new ExpressionStatement(subExpression);
    }

    private Expression parseExpression() {
        return parseAssignment();
    }

    private Expression parseAssignment() {
        var result = parseLogicalOp();

        if (match(TokenType.EQUAL)) {
            consume(TokenType.EQUAL);
            if (!(result instanceof VarExpression) && !(result instanceof DotExpression)) {
                throw panic("Left sub expression of an assignment expression must be either an identifier or '.' expression.\n");
            }
            result = new VarAssignment(result, parseExpression());
        }

        return result;
    }

    private Expression parseLogicalOp() {
        return parseLogicalOpChain(
                this::parseBinaryOpEqualityComparison,
                TokenType.AND,
                TokenType.OR);
    }

    private Expression parseBinaryOpEqualityComparison() {
        return parseBinaryOpChain(
                this::parseBinaryOpInequalityComparison,
                TokenType.EQUAL_EQUAL,
                TokenType.BANGS_EQUAL);
    }

    private Expression parseBinaryOpInequalityComparison() {
        return parseBinaryOpChain(
                this::parseBinaryOpAddOrSub,
                TokenType.LESS_THAN,
                TokenType.LESS_THAN_OR_EQUAL,
                TokenType.MORE_THAN,
                TokenType.MORE_THAN_OR_EQUAL);
    }

    private Expression parseBinaryOpAddOrSub() {
        return parseBinaryOpChain(
                this::parseBinaryOpMulOrDiv,
                TokenType.ADD,
                TokenType.SUB);
    }

    private Expression parseBinaryOpMulOrDiv() {
        return parseBinaryOpChain(
                this::parseUnaryOpChain,
                TokenType.MUL,
                TokenType.DIV);
    }

    private Expression parseLogicalOpChain(
            GetNextExpression getNextExpr,
            TokenType... tokenTypes
    ) {
        return parseLhsRhsOpChain(getNextExpr, LogicalExpression::new, tokenTypes);
    }

    private Expression parseBinaryOpChain(
            GetNextExpression getNextExpr,
            TokenType... tokenTypes
    ) {
        return parseLhsRhsOpChain(getNextExpr, BinaryExpression::new, tokenTypes);
    }

    private Expression parseLhsRhsOpChain(
            GetNextExpression getNextExpr,
            CreateLhsRhsOpExpression createLhsRhsOpExpr,
            TokenType... tokenTypes
    ) {
        var result = getNextExpr.get();
        while (match(tokenTypes)) {
            final var operator = peek();
            consume(tokenTypes);
            final var lhsSubExpression = result;
            final var rhsSubExpression = getNextExpr.get();
            result = createLhsRhsOpExpr.get(
                    operator,
                    lhsSubExpression,
                    rhsSubExpression);
        }
        return result;
    }

    private Expression parseUnaryOpChain() {
        final var unaryOpTokenTypes = new TokenType[] { TokenType.BANGS, TokenType.ADD, TokenType.SUB };

        final var precedingOps = new Stack<Token>();
        while (match(unaryOpTokenTypes)) {
            precedingOps.push(peek());
            consume(unaryOpTokenTypes);
        }

        var result = parseAtomic();
        while (!precedingOps.isEmpty()) {
            final var precedingOp = precedingOps.pop();
            result = new UnaryExpression(precedingOp, result);
        }

        return result;
    }

    private Expression parseAtomic() {
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
            final var result = new LiteralExpression(peek());
            consume(literalTokenTypes);
            return result;
        }

        throw panic(String.format(
                "Token type '%s' is not a valid start for the production 'Atomic'.",
                peek().getType().name()));
    }

    private Expression parseDotAndFunInvocationChain() {
        var result = parseInvokable();
        while (match(TokenType.DOT, TokenType.LFT_PARENTHESIS)) {
            if (match(TokenType.DOT)) {
                consume(TokenType.DOT);
                final var lhsExpression = result;
                final var rhsIdentifier = peek();
                consume(TokenType.IDENTIFIER);
                result = new DotExpression(lhsExpression, rhsIdentifier);
            } else {
                final var subExpression = result;
                final var arguments = parseArgumentList();
                result = new FunInvocation(
                        peek().getFile(),
                        peek().getLine(),
                        subExpression,
                        arguments);
            }
        }
        return result;
    }

    private List<Expression> parseArgumentList() {
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

    private Expression parseInvokable() {
        if (match(TokenType.NEW)) {
            return parseNewInvocation();
        } else if (match(TokenType.LFT_PARENTHESIS)) {
            return parseGrouping();
        } else {
            return parseIdentifier();
        }
    }

    private Expression parseNewInvocation() {
        final var file = peek().getFile();
        final var line = peek().getLine();

        consume(TokenType.NEW);
        final var firstIdentifier = peek();
        consume(TokenType.IDENTIFIER);
        Expression subExpression = new VarExpression(firstIdentifier);
        while (match(TokenType.DOT)) {
            consume(TokenType.DOT);
            final var rhsIdentifier = peek();
            consume(TokenType.IDENTIFIER);
            subExpression = new DotExpression(subExpression, rhsIdentifier);
        }

        final var arguments = parseArgumentList();

        return new NewInvocation(file, line, subExpression, arguments);
    }

    private Expression parseGrouping() {
        consume(TokenType.LFT_PARENTHESIS);
        final var subExpression = parseExpression();
        consume(TokenType.RGT_PARENTHESIS);

        return new GroupingExpression(subExpression);
    }

    private Expression parseIdentifier() {
        final var identifier = peek();
        consume(TokenType.IDENTIFIER);

        return new VarExpression(identifier);
    }

    private void consume(TokenType... tokenTypes) {
        if (!match(tokenTypes)) {
            final var tokenTypesStringBuilder = new StringBuilder();
            for (final var tokenType : tokenTypes) {
                tokenTypesStringBuilder.append("'");
                tokenTypesStringBuilder.append(tokenType.name());
                tokenTypesStringBuilder.append("'");
                tokenTypesStringBuilder.append(",");
            }
            throw panic(String.format(
                    "Expected token types [%s], but found token type '%s'.",
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
        throw new ParserException(peek().getFile(), peek().getLine(), what);
    }

    private Token peek() {
        return _tokens.get(_index);
    }

    private final List<Token> _tokens;
    private int _index = 0;
}
