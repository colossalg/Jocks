package com.colossalg.dataTypes.primitives;

import com.colossalg.dataTypes.JocksValue;

public class JocksBool extends JocksValue {

    @SuppressWarnings("SpellCheckingInspection")
    public static final JocksBool Truthy = new JocksBool(true);

    @SuppressWarnings("SpellCheckingInspection")
    public static final JocksBool Falsey = new JocksBool(false);

    public static JocksBool fromBoolean(boolean val) {
        return val ? Truthy : Falsey;
    }

    @Override
    public String str() {
        return _data ? "true" : "false";
    }

    @Override
    public JocksValue equal(JocksValue other) {
        if (!(other instanceof JocksBool)) {
            return Falsey;
        }
        return fromBoolean(_data == ((JocksBool)other)._data);
    }

    @Override
    public JocksValue notEqual(JocksValue other) {
        if (!(other instanceof JocksBool)) {
            return Truthy;
        }
        return fromBoolean(_data != ((JocksBool)other)._data);
    }

    @Override
    public JocksValue not() {
        return fromBoolean(!_data);
    }

    private JocksBool(boolean data) {
        _data = data;
    }

    private final boolean _data;
}
