package com.colossalg.expression;

import com.colossalg.Token;

import java.util.List;

public class NewInvocation implements Expression {

    public NewInvocation(
            String file,
            int line,
            Token identifier,
            List<Expression> arguments
    ) {
        _file = file;
        _line = line;
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

    public String getFile() {
        return _file;
    }

    public int getLine() {
        return _line;
    }

    public Token getIdentifier() {
        return _identifier;
    }

    public List<Expression> getArguments() {
        return _arguments;
    }

    private int _symbolTableDepth = 0;
    private final String _file;
    private final int _line;
    private final Token _identifier;
    private final List<Expression> _arguments;
}
