package com.colossalg.builtin.functions.maths;

import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksJavaLandFunction;
import com.colossalg.dataTypes.primitives.JocksNumber;

import java.util.List;

public class Abs extends JocksJavaLandFunction {

    public Abs() {
        super("abs");
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public JocksValue call(List<JocksValue> arguments) {
        if (!(arguments.getFirst() instanceof JocksNumber x)) {
            throw new IllegalStateException("abs() expects arguments to be of type JocksNumber.");
        }

        return new JocksNumber(Math.abs(x.getData()));
    }
}
