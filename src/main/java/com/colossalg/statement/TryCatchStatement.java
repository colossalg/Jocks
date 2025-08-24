package com.colossalg.statement;

public class TryCatchStatement implements Statement {

    public TryCatchStatement() {
        // TODO
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitTryCatchStatement(this);
    }
}
