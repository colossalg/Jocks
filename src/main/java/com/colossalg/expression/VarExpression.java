package com.colossalg.expression;

import com.colossalg.Token;

public class VarExpression extends Expression {

    public VarExpression(
            String file,
            int line,
            Token identifier
    ) {
        super(file, line);
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

    private int _symbolTableDepth = -1;
    private final Token _identifier;
}
