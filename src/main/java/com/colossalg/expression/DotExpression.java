package com.colossalg.expression;

import com.colossalg.Token;

public class DotExpression extends Expression {

    public DotExpression(
            String file,
            int line,
            Expression lhsExpression,
            Token rhsIdentifier
    ) {
        super(file, line);
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
