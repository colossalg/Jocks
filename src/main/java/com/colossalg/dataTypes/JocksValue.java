package com.colossalg.dataTypes;

import java.util.Optional;

public abstract class JocksValue {

    public static <T> Optional<T> cast(JocksValue value, Class<T> type) {
        return type.isAssignableFrom(value.getClass())
                ? Optional.of(type.cast(value))
                : Optional.empty();
    }

    public String str() {
        throw new UnsupportedOperationException("Str is not implemented by " + getClass().getName());
    }

    public JocksValue equal(JocksValue other) {
        throw new UnsupportedOperationException("Equal is not implemented by " + getClass().getName());
    }

    public JocksValue notEqual(JocksValue other) {
        throw new UnsupportedOperationException("NotEqual is not implemented by " + getClass().getName());
    }

    public JocksValue lessThan(JocksValue other) {
        throw new UnsupportedOperationException("LessThan is not implemented by " + getClass().getName());
    }

    public JocksValue lessThanOrEqual(JocksValue other) {
        throw new UnsupportedOperationException("LessThanOrEqual is not implemented by " + getClass().getName());
    }

    public JocksValue moreThan(JocksValue other) {
        throw new UnsupportedOperationException("MoreThan is not implemented by " + getClass().getName());
    }

    public JocksValue moreThanOrEqual(JocksValue other) {
        throw new UnsupportedOperationException("MoreThanOrEqual is not implemented by " + getClass().getName());
    }

    public JocksValue not() {
        throw new UnsupportedOperationException("Not is not implemented by " + getClass().getName());
    }

    public JocksValue add() {
        throw new UnsupportedOperationException("Add (unary) is not implemented by " + getClass().getName());
    }

    public JocksValue add(JocksValue other) {
        throw new UnsupportedOperationException("Add (binary) is not implemented by " + getClass().getName());
    }

    public JocksValue sub() {
        throw new UnsupportedOperationException("Sub (unary) is not implemented by " + getClass().getName());
    }

    public JocksValue sub(JocksValue other) {
        throw new UnsupportedOperationException("Sub (binary) is not implemented by " + getClass().getName());
    }

    public JocksValue mul(JocksValue other) {
        throw new UnsupportedOperationException("Mul is not implemented by " + getClass().getName());
    }

    public JocksValue div(JocksValue other) {
        throw new UnsupportedOperationException("Div is not implemented by " + getClass().getName());
    }
}
