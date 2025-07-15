package com.colossalg.builtin.functions.maths;

import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksJavaLandFunction;
import com.colossalg.dataTypes.primitives.JocksNumber;

import java.util.List;

public class Pow extends JocksJavaLandFunction {

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public JocksValue call(List<JocksValue> arguments) {
        if (!(arguments.get(0) instanceof JocksNumber x) || !(arguments.get(1) instanceof JocksNumber y)) {
            throw new IllegalStateException("Pow expects arguments to both be of type JocksNumber.");
        }

        return new JocksNumber(Math.pow(x.getData(), y.getData()));
    }
}
