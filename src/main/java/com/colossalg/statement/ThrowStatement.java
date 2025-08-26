package com.colossalg.statement;

import com.colossalg.expression.Expression;

public class ThrowStatement implements Statement {

    public ThrowStatement(Expression subExpression) {
        _subExpression = subExpression;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitThrowStatement(this);
    }

    public Expression getSubExpression() {
        return _subExpression;
    }

    private final Expression _subExpression;
}
