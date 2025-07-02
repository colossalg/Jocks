package com.colossalg.dataTypes.classes;

import com.colossalg.dataTypes.JocksValue;

import java.util.HashMap;
import java.util.Optional;

public class JocksInstance extends JocksValue {

    public JocksInstance(String className) {
        _className = className;
    }

    public String getClassName() {
        return _className;
    }

    public Optional<JocksValue> getProperty(String identifier) {
        return Optional.ofNullable(
                _properties.getOrDefault(identifier, null));
    }

    public void setProperty(String identifier, JocksValue value) {
        _properties.put(identifier, value);
    }

    private final String _className;
    private final HashMap<String, JocksValue> _properties = new HashMap<>();
}
