package com.colossalg.statement;

import java.util.List;

public class BlockStatement extends Statement {

    public BlockStatement(
            String file,
            int line,
            List<Statement> subStatements
    ) {
        super(file, line);
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
