package com.colossalg.expression;

import com.colossalg.Token;

import java.util.List;

public class NewInvocation implements Expression {

    public NewInvocation(Token identifier, List<Expression> arguments) {
        _identifier = identifier;
        _arguments  = arguments;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitNewInvocation(this);
    }

    public int getSymbolTableDepth() {
        return _symbolTableDepth;
    }

    public void setSymbolTableDepth(int symbolTableDepth) {
        _symbolTableDepth = symbolTableDepth;
    }

    public Token getIdentifier() {
        return _identifier;
    }

    public List<Expression> getArguments() {
        return _arguments;
    }

    private int _symbolTableDepth = 0;
    private final Token _identifier;
    private final List<Expression> _arguments;
}
