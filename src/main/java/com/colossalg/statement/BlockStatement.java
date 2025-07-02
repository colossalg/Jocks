package com.colossalg.statement;

import java.util.List;

public class BlockStatement implements Statement {

    public BlockStatement(List<Statement> subStatements) {
        _subStatements = subStatements;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitBlockStatement(this);
    }

    public List<Statement> getSubStatements() {
        return _subStatements;
    }

    private final List<Statement> _subStatements;
}
