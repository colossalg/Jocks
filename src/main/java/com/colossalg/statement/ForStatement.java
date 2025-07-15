package com.colossalg.statement;

import com.colossalg.expression.Expression;

import java.util.Optional;

public class ForStatement extends Statement {

    public ForStatement(
            String file,
            int line,
            Statement initializer,
            Expression condition,
            Expression increment,
            Statement subStatement
    ) {
        super(file, line);
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
