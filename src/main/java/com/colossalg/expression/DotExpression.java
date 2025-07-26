package com.colossalg.expression;

import com.colossalg.Token;

public class DotExpression implements Expression {

    public DotExpression(Expression lhsExpression, Token rhsIdentifier) {
        _lhsExpression = lhsExpression;
        _rhsIdentifier = rhsIdentifier;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitDotExpression(this);
    }

    public Expression getLhsExpression() {
        return _lhsExpression;
    }

    public Token getRhsIdentifier() {
        return _rhsIdentifier;
    }

    private final Expression _lhsExpression;
    private final Token _rhsIdentifier;
}
