package com.colossalg.statement;

public interface Statement {

    public abstract <T> T accept(StatementVisitor<T> visitor);
}
