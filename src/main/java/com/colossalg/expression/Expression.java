package com.colossalg.expression;

public interface Expression {

    <T> T accept(ExpressionVisitor<T> visitor);
}
