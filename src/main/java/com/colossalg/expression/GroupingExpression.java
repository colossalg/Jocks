package com.colossalg.expression;

public class GroupingExpression implements Expression {

    public GroupingExpression(Expression subExpression) {
        _subExpression = subExpression;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitGroupingExpression(this);
    }

    public Expression getSubExpression() {
        return _subExpression;
    }

    private final Expression _subExpression;
}
