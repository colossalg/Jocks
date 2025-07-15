package com.colossalg.dataTypes;

import com.colossalg.dataTypes.primitives.JocksBool;

public abstract class JocksValue {

    public static <T> T cast(JocksValue value, Class<T> type) {
        if (!type.isAssignableFrom(value.getClass())) {
            throw new IllegalStateException("Internal casting error - Value was expected to have type " + type.getTypeName() + ".");
        }
        return type.cast(value);
    }

    public String str() {
        return getClass().getName();
    }

    public JocksBool equal(JocksValue other) {
        throw new UnsupportedOperationException("Equal is not implemented by " + getClass().getName());
    }

    public JocksBool notEqual(JocksValue other) {
        throw new UnsupportedOperationException("NotEqual is not implemented by " + getClass().getName());
    }

    public JocksBool lessThan(JocksValue other) {
        throw new UnsupportedOperationException("LessThan is not implemented by " + getClass().getName());
    }

    public JocksBool lessThanOrEqual(JocksValue other) {
        throw new UnsupportedOperationException("LessThanOrEqual is not implemented by " + getClass().getName());
    }

    public JocksBool moreThan(JocksValue other) {
        throw new UnsupportedOperationException("MoreThan is not implemented by " + getClass().getName());
    }

    public JocksBool moreThanOrEqual(JocksValue other) {
        throw new UnsupportedOperationException("MoreThanOrEqual is not implemented by " + getClass().getName());
    }

    public JocksBool not() {
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
