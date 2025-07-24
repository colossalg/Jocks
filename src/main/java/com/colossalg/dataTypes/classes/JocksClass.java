package com.colossalg.dataTypes.classes;

import com.colossalg.Token;
import com.colossalg.builtin.functions.NoopConstructor;
import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksFunction;

import java.util.HashMap;
import java.util.Optional;

public class JocksClass extends JocksValue {

    public JocksClass(Token identifier, JocksClass superClass, HashMap<String, JocksFunction> methods) {
        _identifier = identifier;
        _superClass = superClass;
        _methods = methods;

        if (getMethod("__init__").isEmpty()) {
            _methods.put("__init__", new NoopConstructor());
        }
    }

    @Override
    public String str() {
        return String.format("Class(%s)", _identifier.getText());
    }

    public JocksInstance createInstance() {
        return new JocksInstance(this);
    }

    public Token getIdentifier() {
        return _identifier;
    }

    public Optional<JocksFunction> getMethod(String identifier) {
        return Optional.ofNullable(
                _methods.getOrDefault(identifier, null));
    }

    public Optional<JocksFunction> getMethodRecursive(String identifier) {
        return getMethod(identifier)
                .or(() -> _superClass == null
                    ? Optional.empty()
                    : _superClass.getMethodRecursive(identifier));
    }

    private final Token _identifier;
    private final JocksClass _superClass;
    private final HashMap<String, JocksFunction> _methods;
}
