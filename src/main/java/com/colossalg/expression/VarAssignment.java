package com.colossalg.expression;

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

    public Expression getLhsExpression() {
        return _lhsExpression;
    }

    public Expression getRhsExpression() {
        return _rhsExpression;
    }

    private final Expression _lhsExpression;
    private final Expression _rhsExpression;
}
