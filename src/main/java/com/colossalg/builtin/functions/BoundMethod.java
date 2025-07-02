package com.colossalg.builtin.functions;

import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.classes.JocksInstance;
import com.colossalg.dataTypes.functions.JocksFunction;
import com.colossalg.dataTypes.functions.JocksJavaLandFunction;

import java.util.ArrayList;
import java.util.List;

public class BoundMethod extends JocksJavaLandFunction {

    public BoundMethod(JocksInstance instance, JocksFunction function) {
        _instance = instance;
        _function = function;
    }

    @Override
    public int getArity() {
        return _function.getArity() - 1;
    }

    @Override
    public JocksValue call(List<JocksValue> arguments) {
        final var argumentsCopy = new ArrayList<JocksValue>();
        argumentsCopy.add(_instance);
        argumentsCopy.addAll(arguments);
        return _function.call(argumentsCopy);
    }

    private final JocksInstance _instance;
    private final JocksFunction _function;
}
