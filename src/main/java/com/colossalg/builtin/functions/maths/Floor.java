package com.colossalg.builtin.functions.maths;

import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksJavaLandFunction;
import com.colossalg.dataTypes.primitives.JocksNumber;

import java.util.List;

public class Floor extends JocksJavaLandFunction {

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public JocksValue call(List<JocksValue> arguments) {
        if (!(arguments.getFirst() instanceof JocksNumber x)) {
            // TODO - Need to suss out how to actually handle run time errors.
            //        Need a way to throw/catch errors within the language itself.
            throw new IllegalStateException("Arguments was expected to be of type JocksNumber.");
        }

        return new JocksNumber(Math.floor(x.getData()));
    }
}
