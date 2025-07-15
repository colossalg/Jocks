package com.colossalg.statement;

import com.colossalg.expression.Expression;

public class PrintStatement extends Statement {

    public PrintStatement(
            String file,
            int line,
            Expression subExpression
    ) {
        super(file, line);
        _subExpression = subExpression;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitPrintStatement(this);
    }

    public Expression getSubExpression() {
        return _subExpression;
    }

    private final Expression _subExpression;
}
