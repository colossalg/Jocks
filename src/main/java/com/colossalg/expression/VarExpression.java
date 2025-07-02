package com.colossalg.expression;

import com.colossalg.Token;

public class VarExpression implements Expression {

    public VarExpression(Token identifier) {
        _identifier = identifier;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitVarExpression(this);
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

    private int _symbolTableDepth = 0;
    private final Token _identifier;
}
