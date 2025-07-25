package com.colossalg.statement;

import com.colossalg.Token;

import java.util.List;

public class FunDeclaration implements Statement {

    public FunDeclaration(
            Token identifier,
            List<Token> parameters,
            List<Statement> statements
    ) {
        _identifier = identifier;
        _parameters = parameters;
        _statements = statements;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitFunDeclaration(this);
    }

    public Token getIdentifier() {
        return _identifier;
    }

    public List<Token> getParameters() {
        return _parameters;
    }

    public List<Statement> getStatements() {
        return _statements;
    }

    private final Token _identifier;
    private final List<Token> _parameters;
    private final List<Statement> _statements;
}
