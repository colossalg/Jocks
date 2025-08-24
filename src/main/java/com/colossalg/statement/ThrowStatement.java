package com.colossalg.statement;

public class ThrowStatement implements Statement {

    public ThrowStatement() {
        // TODO
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitThrowStatement(this);
    }
}
