package com.colossalg.expression;

import com.colossalg.Token;

public class UnaryExpression extends Expression {

    public UnaryExpression(
            String file,
            int line,
            Token operator,
            Expression subExpression
    ) {
        super(file, line);
        _operator = operator;
        _subExpression = subExpression;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitUnaryExpression(this);
    }

    public Token getOperator() {
        return _operator;
    }

    public Expression getSubExpression() {
        return _subExpression;
    }

    private final Token _operator;
    private final Expression _subExpression;
}
