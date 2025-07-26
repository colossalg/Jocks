package com.colossalg.dataTypes.functions;

import com.colossalg.dataTypes.JocksValue;

import java.util.List;

public abstract class JocksFunction extends JocksValue {

    public JocksFunction(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public abstract int getArity();

    public abstract JocksValue call(List<JocksValue> arguments);

    private final String _name;
}
