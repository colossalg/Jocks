package com.colossalg.expression;

import com.colossalg.Token;

import java.util.List;

public class FunInvocation implements Expression {

    public FunInvocation(Expression subExpression, List<Expression> arguments) {
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

    private int _symbolTableDepth = 0;
    private final Expression _subExpression;
    private final List<Expression> _arguments;
}
