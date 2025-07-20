package com.colossalg.dataTypes.primitives;

import com.colossalg.dataTypes.JocksValue;

public class JocksNil extends JocksValue {

    public static final JocksNil Instance = new JocksNil();

    @Override
    public String str() {
        return "nil";
    }

    @Override
    public JocksValue equal(JocksValue other) {
        return JocksBool.fromBoolean(other == Instance);
    }

    @Override
    public JocksBool notEqual(JocksValue other) {
        return JocksBool.fromBoolean(other != Instance);
    }

    private JocksNil() {}
}
