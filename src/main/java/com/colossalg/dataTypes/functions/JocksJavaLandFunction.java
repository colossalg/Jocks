package com.colossalg.dataTypes.functions;

public abstract class JocksJavaLandFunction extends JocksFunction {

    public JocksJavaLandFunction(String name) {
        super(name);
    }

    @Override
    public String str() {
        return String.format("JocksJavaLandFunction(%s)", getName());
    }
}
