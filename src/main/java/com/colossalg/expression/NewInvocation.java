package com.colossalg.expression;

import com.colossalg.Token;

import java.util.List;

public class NewInvocation extends Expression {

    public NewInvocation(
            String file,
            int line,
            Token identifier,
            List<Expression> arguments
    ) {
        super(file, line);
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
