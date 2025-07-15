package com.colossalg.statement;

import com.colossalg.expression.Expression;

public class WhileStatement extends Statement {

    public WhileStatement(
            String file,
            int line,
            Expression condition,
            Statement subStatement
    ) {
        super(file, line);
        _condition = condition;
        _subStatement = subStatement;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitWhileStatement(this);
    }

    public Expression getCondition() {
        return _condition;
    }

    public Statement getSubStatement() {
        return _subStatement;
    }

    private final Expression _condition;
    private final Statement _subStatement;
}
