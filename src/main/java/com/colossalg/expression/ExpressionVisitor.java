package com.colossalg.expression;

public interface ExpressionVisitor<T> {

    @SuppressWarnings("unused") // False alarm, this has many usages.
    T visit(Expression expression);

    T visitLogicalExpression(LogicalExpression expression);

    T visitBinaryExpression(BinaryExpression expression);

    T visitUnaryExpression(UnaryExpression expression);

    T visitGroupingExpression(GroupingExpression expression);

    T visitDotExpression(DotExpression expression);

    T visitFunInvocation(FunInvocation expression);

    T visitNewInvocation(NewInvocation expression);

    T visitVarAssignment(VarAssignment expression);

    T visitVarExpression(VarExpression expression);

    T visitLiteralExpression(LiteralExpression expression);
}
