package com.colossalg.expression;

import com.colossalg.Token;

public class VarAssignment extends Expression {

    public VarAssignment(
            String file,
            int line,
            Expression lhsExpression,
            Expression rhsExpression
    ) {
        super(file, line);
        _lhsExpression = lhsExpression;
        _rhsExpression = rhsExpression;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitVarAssignment(this);
    }

    public int getSymbolTableDepth() {
        return _symbolTableDepth;
    }

    public void setSymbolTableDepth(int symbolTableDepth) {
        _symbolTableDepth = symbolTableDepth;
    }

    public Expression getLhsExpression() {
        return _lhsExpression;
    }

    public Expression getRhsExpression() {
        return _rhsExpression;
    }

    private int _symbolTableDepth = 0;
    private final Expression _lhsExpression;
    private final Expression _rhsExpression;
}
