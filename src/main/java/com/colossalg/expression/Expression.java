package com.colossalg.expression;

public interface Expression {

    public abstract <T> T accept(ExpressionVisitor<T> visitor);
}
