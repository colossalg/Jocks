package com.colossalg.dataTypes.primitives;

import com.colossalg.dataTypes.JocksValue;

public class JocksBool extends JocksValue {

    @SuppressWarnings("SpellCheckingInspection")
    public static final JocksBool Truthy = new JocksBool(true);

    @SuppressWarnings("SpellCheckingInspection")
    public static final JocksBool Falsey = new JocksBool(false);

    @Override
    public String str() {
        return _data ? "true" : "false";
    }

    @Override
    public JocksBool equal(JocksValue other) {
        if (!(other instanceof JocksBool)) {
            return Falsey;
        }

        return _data == ((JocksBool)other)._data
                ? Truthy
                : Falsey;
    }

    @Override
    public JocksBool notEqual(JocksValue other) {
        return equal(other).not();
    }

    @Override
    public JocksBool not() {
        return _data ? Falsey : Truthy;
    }

    private JocksBool(boolean data) {
        _data = data;
    }

    private final boolean _data;
}
