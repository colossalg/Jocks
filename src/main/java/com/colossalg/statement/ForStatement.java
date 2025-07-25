package com.colossalg.statement;

import com.colossalg.expression.Expression;

import java.util.Optional;

public class ForStatement implements Statement {

    public ForStatement(
            Statement initializer,
            Expression condition,
            Expression increment,
            Statement subStatement
    ) {
        _initializer = initializer;
        _condition = condition;
        _increment = increment;
        _subStatement = subStatement;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitForStatement(this);
    }

    public Optional<Statement> getInitializer() {
        return Optional.ofNullable(_initializer);
    }

    public Optional<Expression> getCondition() {
        return Optional.ofNullable(_condition);
    }

    public Optional<Expression> getIncrement() {
        return Optional.ofNullable(_increment);
    }

    public Statement getSubStatement() {
        return _subStatement;
    }

    private final Statement _initializer;
    private final Expression _condition;
    private final Expression _increment;
    private final Statement _subStatement;
}
