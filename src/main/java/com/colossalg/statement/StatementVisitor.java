package com.colossalg.statement;

public interface StatementVisitor<T> {

    @SuppressWarnings("unused") // False alarm, this has many usages.
    T visit(Statement statement);

    T visitClassDeclaration(ClassDeclaration statement);

    T visitFunDeclaration(FunDeclaration statement);

    T visitVarDeclaration(VarDeclaration statement);

    T visitIfElseStatement(IfElseStatement statement);

    T visitWhileStatement(WhileStatement statement);

    T visitForStatement(ForStatement statement);

    T visitTryCatchStatement(TryCatchStatement statement);

    T visitThrowStatement(ThrowStatement statement);

    T visitBlockStatement(BlockStatement statement);

    T visitReturnStatement(ReturnStatement statement);

    T visitPrintStatement(PrintStatement statement);

    T visitExpressionStatement(ExpressionStatement statement);
}
