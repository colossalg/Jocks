package com.colossalg.statement;

import com.colossalg.expression.Expression;

import java.util.Optional;

public class ReturnStatement implements Statement {

    public ReturnStatement(
            String file,
            int line,
            Expression subExpression
    ) {
        _file = file;
        _line = line;
        _subExpression = subExpression;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitReturnStatement(this);
    }

    public String getFile() {
        return _file;
    }

    public int getLine() {
        return _line;
    }

    public Optional<Expression> getSubExpression() {
        return Optional.ofNullable(_subExpression);
    }

    private final String _file;
    private final int _line;
    private final Expression _subExpression;
}
