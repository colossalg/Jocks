package com.colossalg.statement;

import com.colossalg.expression.Expression;

import java.util.Optional;

public class IfElseStatement extends Statement {

    public IfElseStatement(
            String file,
            int line,
            Expression condition,
            Statement thenSubStatement,
            Statement elseSubStatement
    ) {
        super(file, line);
        _condition = condition;
        _thenSubStatement = thenSubStatement;
        _elseSubStatement = elseSubStatement;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitIfElseStatement(this);
    }

    public Expression getCondition() {
        return _condition;
    }

    public Statement getThenSubStatement() {
        return _thenSubStatement;
    }

    public Optional<Statement> getElseSubStatement() {
        return Optional.ofNullable(_elseSubStatement);
    }

    private final Expression _condition;
    private final Statement _thenSubStatement;
    private final Statement _elseSubStatement;
}
