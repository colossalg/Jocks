package com.colossalg.statement;

public interface Statement {

    <T> T accept(StatementVisitor<T> visitor);
}
