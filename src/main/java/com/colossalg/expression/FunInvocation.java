package com.colossalg.expression;

import java.util.List;

public class FunInvocation extends Expression {

    public FunInvocation(
            String file,
            int line,
            Expression subExpression,
            List<Expression> arguments
    ) {
        super(file, line);
        _subExpression = subExpression;
        _arguments  = arguments;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitFunInvocation(this);
    }

    public Expression getSubExpression() {
        return _subExpression;
    }

    public List<Expression> getArguments() {
        return _arguments;
    }

    private final Expression _subExpression;
    private final List<Expression> _arguments;
}
