package com.colossalg.dataTypes.primitives;

import com.colossalg.dataTypes.JocksValue;

public class JocksString extends JocksValue {

    public JocksString(String data) {
        _data = data;
    }

    @Override
    public String str() {
        return _data;
    }

    @Override
    public JocksBool equal(JocksValue other) {
        if (!(other instanceof JocksString)) {
            return JocksBool.Falsey;
        }

        return _data.equals(((JocksString)other)._data)
                ? JocksBool.Truthy
                : JocksBool.Falsey;
    }

    @Override
    public JocksBool notEqual(JocksValue other) {
        return equal(other).not();
    }

    @Override
    public JocksValue add(JocksValue other) {
        if (!(other instanceof JocksString)) {
            throw new IllegalArgumentException("Argument 'other' must have type JocksString, was '" + other.getClass().getName() + "'.");
        }

        return new JocksString(_data + ((JocksString)other)._data);
    }

    private final String _data;
}
