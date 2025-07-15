package com.colossalg.expression;

import com.colossalg.Token;

public class LogicalExpression extends Expression {

    public LogicalExpression(
            String file,
            int line,
            Token operator,
            Expression lftSubExpression,
            Expression rgtSubExpression
    ) {
        super(file, line);
        _operator = operator;
        _lftSubExpression = lftSubExpression;
        _rgtSubExpression = rgtSubExpression;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitLogicalExpression(this);
    }

    public Token getOperator() {
        return _operator;
    }

    public Expression getLftSubExpression() {
        return _lftSubExpression;
    }

    public Expression getRgtSubExpression() {
        return _rgtSubExpression;
    }

    private final Token _operator;
    private final Expression _lftSubExpression;
    private final Expression _rgtSubExpression;
}
