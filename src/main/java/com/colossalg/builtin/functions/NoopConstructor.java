package com.colossalg.builtin.functions;

import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksJavaLandFunction;
import com.colossalg.dataTypes.primitives.JocksNil;

import java.util.List;

public class NoopConstructor extends JocksJavaLandFunction {

    public NoopConstructor(String className) {
        super(className + ".__init__");
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public JocksValue call(List<JocksValue> arguments) {
        return JocksNil.Instance;
    }
}
