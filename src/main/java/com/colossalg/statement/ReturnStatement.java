package com.colossalg.statement;

import com.colossalg.expression.Expression;

import java.util.Optional;

public class ReturnStatement extends Statement {

    public ReturnStatement(
            String file,
            int line,
            Expression subExpression
    ) {
        super(file, line);
        _subExpression = subExpression;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitReturnStatement(this);
    }

    public Optional<Expression> getSubExpression() {
        return Optional.ofNullable(_subExpression);
    }

    private final Expression _subExpression;
}
