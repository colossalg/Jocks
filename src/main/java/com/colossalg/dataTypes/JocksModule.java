package com.colossalg.dataTypes;

import java.io.File;
import java.util.HashMap;

public class JocksModule extends JocksPropertyCollection {

    public JocksModule(String path, HashMap<String, JocksValue> exports) {
        super(exports);
        _path = path;
    }

    @Override
    public String str() {
        return "<module: " + (new File(_path).getName()) + ">";
    }

    private final String _path;
}
