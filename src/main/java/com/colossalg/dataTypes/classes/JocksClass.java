package com.colossalg.dataTypes.classes;

import com.colossalg.builtin.functions.NoopConstructor;
import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksFunction;

import java.util.HashMap;
import java.util.Optional;

public class JocksClass extends JocksValue {

    public JocksClass(String identifier, String superClass, HashMap<String, JocksFunction> methods) {
        _identifier = identifier;
        _superClass = superClass;
        _methods = methods;

        if (getMethod("__init__").isEmpty()) {
            _methods.put("__init__", new NoopConstructor());
        }
    }

    public JocksInstance createInstance() {
        return new JocksInstance(_identifier);
    }

    public String getIdentifier() {
        return _identifier;
    }

    public String getSuperClass() {
        return _superClass;
    }

    public HashMap<String, JocksFunction> getMethods() {
        return _methods;
    }

    public Optional<JocksFunction> getMethod(String identifier) {
        return Optional.ofNullable(
                _methods.getOrDefault(identifier, null));
    }

    private final String _identifier;
    private final String _superClass;
    private final HashMap<String, JocksFunction> _methods;
}
