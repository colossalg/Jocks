package com.colossalg.builtin.functions;

import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksJavaLandFunction;
import com.colossalg.dataTypes.primitives.JocksString;

import java.util.List;

public class ToString extends JocksJavaLandFunction {

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public JocksValue call(List<JocksValue> arguments) {
        return new JocksString(arguments.getFirst().str());
    }
}
