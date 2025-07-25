package com.colossalg.statement;

import com.colossalg.expression.Expression;

public class WhileStatement implements Statement {

    public WhileStatement(Expression condition, Statement subStatement) {
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
