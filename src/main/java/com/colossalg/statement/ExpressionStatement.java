package com.colossalg.statement;

import com.colossalg.expression.Expression;

public class ExpressionStatement extends Statement {

    public ExpressionStatement(
            String file,
            int line,
            Expression subExpression
    ) {
        super(file, line);
        _subExpression = subExpression;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitExpressionStatement(this);
    }

    public Expression getSubExpression() {
        return _subExpression;
    }

    private final Expression _subExpression;
}
