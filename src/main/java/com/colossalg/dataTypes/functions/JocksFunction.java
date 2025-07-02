package com.colossalg.dataTypes.functions;

import com.colossalg.dataTypes.JocksValue;

import java.util.List;

public abstract class JocksFunction extends JocksValue {

    public abstract int getArity();

    public abstract JocksValue call(List<JocksValue> arguments);
}
