package com.colossalg.dataTypes.primitives;

import com.colossalg.dataTypes.JocksValue;

public class JocksNumber extends JocksValue {

    public JocksNumber(double data) {
        _data = data;
    }

    @Override
    public String str() {
        return Double.toString(_data);
    }

    @Override
    public JocksValue equal(JocksValue other) {
        if (!(other instanceof JocksNumber)) {
            return JocksBool.Falsey;
        }
        return JocksBool.fromBoolean(_data == ((JocksNumber)other)._data);
    }

    @Override
    public JocksBool notEqual(JocksValue other) {
        if (!(other instanceof JocksNumber)) {
            return JocksBool.Truthy;
        }
        return JocksBool.fromBoolean(_data != ((JocksNumber)other)._data);
    }

    @Override
    public JocksBool lessThan(JocksValue other) {
        assertOtherIsJocksNumber(other);
        return JocksBool.fromBoolean(_data < ((JocksNumber)other)._data);
    }

    @Override
    public JocksValue lessThanOrEqual(JocksValue other) {
        assertOtherIsJocksNumber(other);
        return JocksBool.fromBoolean(_data <= ((JocksNumber)other)._data);
    }

    @Override
    public JocksValue moreThan(JocksValue other) {
        assertOtherIsJocksNumber(other);
        return JocksBool.fromBoolean(_data > ((JocksNumber)other)._data);
    }

    @Override
    public JocksValue moreThanOrEqual(JocksValue other) {
        assertOtherIsJocksNumber(other);
        return JocksBool.fromBoolean(_data >= ((JocksNumber)other)._data);
    }

    @Override
    public JocksValue add() {
        return this;
    }

    @Override
    public JocksValue add(JocksValue other) {
        assertOtherIsJocksNumber(other);
        return new JocksNumber(_data + ((JocksNumber)other)._data);
    }

    @Override
    public JocksValue sub() {
        return new JocksNumber(-1 * _data);
    }

    @Override
    public JocksValue sub(JocksValue other) {
        assertOtherIsJocksNumber(other);
        return new JocksNumber(_data - ((JocksNumber)other)._data);
    }

    @Override
    public JocksValue mul(JocksValue other) {
        assertOtherIsJocksNumber(other);
        return new JocksNumber(_data * ((JocksNumber)other)._data);
    }

    @Override
    public JocksValue div(JocksValue other) {
        assertOtherIsJocksNumber(other);
        return new JocksNumber(_data / ((JocksNumber)other)._data);
    }

    public double getData() {
        return _data;
    }

    private void assertOtherIsJocksNumber(JocksValue other) {
        if (!(other instanceof JocksNumber)) {
            throw new IllegalArgumentException("Argument 'other' must have type JocksNumber, was '" + other.getClass().getName() + "'.");
        }
    }

    private final double _data;
}
