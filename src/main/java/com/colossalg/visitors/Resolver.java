package com.colossalg.visitors;

import com.colossalg.ErrorReporter;
import com.colossalg.JocksError;
import com.colossalg.Token;
import com.colossalg.expression.*;
import com.colossalg.statement.*;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Resolver implements StatementVisitor<Void>, ExpressionVisitor<Void> {

    public Resolver(ErrorReporter errorReporter) {
        _errorReporter = errorReporter;

        begScope(); // Global scope

        // Type checking
        declareAndDefine("is_nil");
        declareAndDefine("is_bool");
        declareAndDefine("is_number");
        declareAndDefine("is_string");
        declareAndDefine("is_instance");
        declareAndDefine("is_function");
        declareAndDefine("is_class");

        // Maths
        declareAndDefine("abs");
        declareAndDefine("floor");
        declareAndDefine("pow");

        // Strings
        declareAndDefine("to_string");

        // Global Object class which all other classes are descendants of.
        declareAndDefine("Object");
    }

    public void visitAll(List<Statement> statements) {
        for (final var statement : statements) {
            visit(statement);
        }
    }

    public Void visit(Statement statement) {
        return statement.accept(this);
    }

    public Void visitClassDeclaration(ClassDeclaration statement) {
        if (_scopes.size() != 1) {
            _errorReporter.report(
                    new JocksError(
                        "Resolver",
                        statement.getIdentifier().getFile(),
                        statement.getIdentifier().getLine(),
                        "Class declarations are only allowed at the global scope."));
        }

        declareAndDefine(statement.getIdentifier());

        if (statement.getSuperClass().isPresent()) {
            // We don't care about the return as the super class must be in the
            // global scope like all classes, but this method will throw if the
            // token is not found.
            getIdentifierSymbolTableDepth(statement.getSuperClass().get());
        }

        begScope();
        declareAndDefine("super");

        for (final var method : statement.getMethods()) {
            visitFunDeclarationBody(method);
        }

        endScope();

        return null;
    }

    public Void visitFunDeclaration(FunDeclaration statement) {
        declareAndDefine(statement.getIdentifier());
        visitFunDeclarationBody(statement);

        return null;
    }

    public Void visitVarDeclaration(VarDeclaration statement) {
        declare(statement.getIdentifier());
        visit(statement.getExpression());
        define(statement.getIdentifier());

        return null;
    }

    public Void visitIfElseStatement(IfElseStatement statement) {
        visit(statement.getCondition());
        visit(statement.getThenSubStatement());
        visitIfNotNull(statement.getElseSubStatement().orElse(null));

        return null;
    }

    public Void visitWhileStatement(WhileStatement statement) {
        visit(statement.getCondition());
        visit(statement.getSubStatement());

        return null;
    }

    public Void visitForStatement(ForStatement statement) {
        begScope();
        visitIfNotNull(statement.getInitializer().orElse(null));
        visitIfNotNull(statement.getCondition().orElse(null));
        visitIfNotNull(statement.getIncrement().orElse(null));
        visit(statement.getSubStatement());
        endScope();

        return null;
    }

    public Void visitBlockStatement(BlockStatement statement) {
        begScope();
        for (final var subStatement : statement.getSubStatements()) {
            visit(subStatement);
        }
        endScope();

        return null;
    }

    public Void visitReturnStatement(ReturnStatement statement) {
        if (!_isWithinFun) {
            _errorReporter.report(
                    new JocksError(
                            "Resolver",
                            statement.getFile(),
                            statement.getLine(),
                            "Return statements are only allowed from within a function or method."));
        }
        visitIfNotNull(statement.getSubExpression().orElse(null));

        return null;
    }

    public Void visitPrintStatement(PrintStatement statement) {
        visit(statement.getSubExpression());

        return null;
    }

    public Void visitExpressionStatement(ExpressionStatement statement) {
        visit(statement.getSubExpression());

        return null;
    }

    public Void visit(Expression expression) {
        return expression.accept(this);
    }

    public Void visitLogicalExpression(LogicalExpression expression) {
        visit(expression.getLftSubExpression());
        visit(expression.getRgtSubExpression());

        return null;
    }

    public Void visitBinaryExpression(BinaryExpression expression) {
        visit(expression.getLftSubExpression());
        visit(expression.getRgtSubExpression());

        return null;
    }

    public Void visitUnaryExpression(UnaryExpression expression) {
        visit(expression.getSubExpression());

        return null;
    }

    public Void visitGroupingExpression(GroupingExpression expression) {
        visit(expression.getSubExpression());

        return null;
    }

    public Void visitDotExpression(DotExpression expression) {
        visit(expression.getLhsExpression());

        return null;
    }

    public Void visitFunInvocation(FunInvocation expression) {
        visit(expression.getSubExpression());
        for (final var argument : expression.getArguments()) {
            visit(argument);
        }

        return null;
    }

    public Void visitNewInvocation(NewInvocation expression) {
        expression.setSymbolTableDepth(
                getIdentifierSymbolTableDepth(expression.getIdentifier()));

        for (final var argument : expression.getArguments()) {
            visit(argument);
        }

        return null;
    }

    public Void visitVarAssignment(VarAssignment expression) {
        visit(expression.getLhsExpression());
        visit(expression.getRhsExpression());

        return null;
    }

    public Void visitVarExpression(VarExpression expression) {
        expression.setSymbolTableDepth(
                getIdentifierSymbolTableDepth(expression.getIdentifier()));

        return null;
    }

    public Void visitLiteralExpression(LiteralExpression expression) {
        return null;
    }

    private void visitFunDeclarationBody(FunDeclaration statement) {
        begScope();

        final var oldIsWithinFun = _isWithinFun;
        _isWithinFun = true;

        for (final var parameter : statement.getParameters()) {
            declareAndDefine(parameter);
        }
        for (final var subStatement : statement.getStatements()) {
            visit(subStatement);
        }

        _isWithinFun = oldIsWithinFun;

        endScope();
    }

    private void visitIfNotNull(Statement statement) {
        if (statement != null) {
            visit(statement);
        }
    }

    private void visitIfNotNull(Expression expression) {
        if (expression != null) {
            visit(expression);
        }
    }

    private void begScope() {
        _scopes.push(new HashMap<>());
    }

    private void endScope() {
        _scopes.pop();
    }

    private void declare(Token token) {
        declare(token.getText());
    }

    private void declare(String identifier) {
        _scopes.peek().put(identifier, false);
    }

    private void define(Token token) {
        define(token.getText());
    }

    private void define(String identifier) {
        _scopes.peek().put(identifier, true);
    }

    private void declareAndDefine(Token token) {
        declareAndDefine(token.getText());
    }

    private void declareAndDefine(String identifier) {
        declare(identifier);
        define(identifier);
    }

    private int getIdentifierSymbolTableDepth(Token token) {
        for (int i = 0; i < _scopes.size(); i++) {
            final var scope = _scopes.get(_scopes.size() - 1 - i);
            if (scope.containsKey(token.getText())) {
                if (!scope.get(token.getText())) {
                    _errorReporter.report(
                            new JocksError(
                                    "Resolver",
                                    token.getFile(),
                                    token.getLine(),
                                    "Attempting to reference undefined variable + '" + token.getText() + "'."));
                }
                return i;
            }
        }

        _errorReporter.report(
                new JocksError(
                        "Resolver",
                        token.getFile(),
                        token.getLine(),
                        "Attempting to reference undeclared variable + '" + token.getText() + "'."));
        return -1;
    }

    private final ErrorReporter _errorReporter;
    private boolean _isWithinFun = false;
    private final Stack<HashMap<String, Boolean>> _scopes = new Stack<>();
}
