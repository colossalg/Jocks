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
    public JocksValue equal(JocksValue other) {
        if (!(other instanceof JocksString)) {
            return JocksBool.Falsey;
        }
        return JocksBool.fromBoolean(_data.equals(((JocksString)other)._data));
    }

    @Override
    public JocksBool notEqual(JocksValue other) {
        if (!(other instanceof JocksString)) {
            return JocksBool.Truthy;
        }
        return JocksBool.fromBoolean(!_data.equals(((JocksString)other)._data));
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
