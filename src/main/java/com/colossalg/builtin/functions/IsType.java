package com.colossalg.builtin.functions;

import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksJavaLandFunction;
import com.colossalg.dataTypes.primitives.JocksBool;

import java.util.List;

// See answers from here:
// https://stackoverflow.com/questions/1570073/java-instanceof-and-generics
public class IsType<T extends JocksValue> extends JocksJavaLandFunction {

    public IsType(Class<T> type) {
        _type = type;
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public JocksValue call(List<JocksValue> arguments) {
        return _type.isAssignableFrom(arguments.getFirst().getClass())
                ? JocksBool.Truthy
                : JocksBool.Falsey;
    }

    private final Class<T> _type;
}
