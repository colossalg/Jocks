package com.colossalg.expression;

import java.util.List;

public class FunInvocation implements Expression {

    public FunInvocation(
            String file,
            int line,
            Expression subExpression,
            List<Expression> arguments
    ) {
        _file = file;
        _line = line;
        _subExpression = subExpression;
        _arguments  = arguments;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitFunInvocation(this);
    }

    public String getFile() {
        return _file;
    }

    public int getLine() {
        return _line;
    }

    public Expression getSubExpression() {
        return _subExpression;
    }

    public List<Expression> getArguments() {
        return _arguments;
    }

    private final String _file;
    private final int _line;
    private final Expression _subExpression;
    private final List<Expression> _arguments;
}
