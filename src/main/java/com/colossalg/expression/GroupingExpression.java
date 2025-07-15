package com.colossalg.expression;

public class GroupingExpression extends Expression {

    public GroupingExpression(
            String file,
            int line,
            Expression subExpression
    ) {
        super(file, line);
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
