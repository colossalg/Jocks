package com.colossalg.dataTypes.primitives;

import com.colossalg.dataTypes.JocksValue;

public class JocksNil extends JocksValue {

    public static final JocksNil Instance = new JocksNil();

    @Override
    public String str() {
        return "nil";
    }

    @Override
    public JocksBool equal(JocksValue other) {
        return other == Instance
                ? JocksBool.Truthy
                : JocksBool.Falsey;
    }

    @Override
    public JocksBool notEqual(JocksValue other) {
        return equal(other).not();
    }

    private JocksNil() {}
}
