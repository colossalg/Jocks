package com.colossalg.visitors;

import com.colossalg.Token;
import com.colossalg.expression.*;
import com.colossalg.statement.*;

import java.util.HashMap;
import java.util.Stack;

// TODO - Variable assignment should take an expression for the LHS rather than a token
//        to facilitate setting properties. Then the resolver should have state for
//        maintaining whether we are currenly in the LHS of an assignment and
//        prevent the use of things such as 'new'.
//
//        This should be legal:
//          (foo.bar())(baz).someProp = someVal;
//
//        This should be illegal:
//          (new Foo()).bar = baz

public class Resolver implements StatementVisitor<Void>, ExpressionVisitor<Void> {

    public Resolver() {
        begScope(); // Global scope

        // Type checking
        declareAndDefine("isNil");
        declareAndDefine("isBool");
        declareAndDefine("isNumber");
        declareAndDefine("isString");
        declareAndDefine("isInstance");
        declareAndDefine("isFunction");
        declareAndDefine("isClass");

        // Maths
        declareAndDefine("floor");
        declareAndDefine("pow");

        // Global Object class which all other classes are descendants of.
        declareAndDefine("Object");
    }

    public Void visit(Statement statement) {
        return statement.accept(this);
    }

    public Void visitClassDeclaration(ClassDeclaration statement) {
        if (_scopes.size() != 1) {
            // TODO - Need to suss out how to actually handle run time errors.
            //        Need a way to throw/catch errors within the language itself.
            throw new IllegalStateException("Class declarations must be at the global scope.");
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
        _isWithinClass = true;

        for (final var method : statement.getMethods()) {
            visitFunDeclarationBody(method);
        }

        _isWithinClass = false;
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
            // TODO - Need to suss out how to actually handle run time errors.
            //        Need a way to throw/catch errors within the language itself.
            throw new IllegalStateException("Return may only be used within a function.");
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
                    // TODO - Need to suss out how to actually handle run time errors.
                    //        Need a way to throw/catch errors within the language itself.
                    throw new IllegalStateException("Attempting to reference undefined variable.");
                } else {
                    return i;
                }
            }
        }

        // TODO - Need to suss out how to actually handle run time errors.
        //        Need a way to throw/catch errors within the language itself.
        throw new IllegalStateException("Attempting to reference undeclared variable.");
    }

    private boolean _isWithinClass = false;
    private boolean _isWithinFun = false;
    private final Stack<HashMap<String, Boolean>> _scopes = new Stack<>();
}
