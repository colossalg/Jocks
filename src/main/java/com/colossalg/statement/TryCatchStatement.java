package com.colossalg.statement;

import com.colossalg.Token;

public class TryCatchStatement implements Statement {

    public TryCatchStatement(
            Statement tryStatement,
            Statement catchStatement,
            Token exceptionIdentifier
    ) {
        _tryStatement = tryStatement;
        _catchStatement = catchStatement;
        _exceptionIdentifier = exceptionIdentifier;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitTryCatchStatement(this);
    }

    public Statement getTryStatement() {
        return _tryStatement;
    }

    public Statement getCatchStatement() {
        return _catchStatement;
    }

    public Token getExceptionIdentifier() {
        return _exceptionIdentifier;
    }

    private final Statement _tryStatement;
    private final Statement _catchStatement;
    private final Token _exceptionIdentifier;
}
