package com.colossalg.dataTypes;

import java.util.HashMap;
import java.util.Optional;

public abstract class JocksPropertyCollection extends JocksValue {

    public JocksPropertyCollection(HashMap<String, JocksValue> properties) {
        _properties = properties;
    }

    public Optional<JocksValue> getProperty(String identifier) {
        return Optional.ofNullable(
                _properties.getOrDefault(identifier, null));
    }

    public void setProperty(String identifier, JocksValue value) {
        _properties.put(identifier, value);
    }

    private final HashMap<String, JocksValue> _properties;
}
