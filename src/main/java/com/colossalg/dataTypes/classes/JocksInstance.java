package com.colossalg.dataTypes.classes;

import com.colossalg.builtin.functions.BoundMethod;
import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class JocksInstance extends JocksValue {

    public JocksInstance(JocksClass jClass) {
        _class = jClass;
    }

    @Override
    public String str() {
        final var strMethod = getMethod("__str__")
                .map((method) -> JocksValue.cast(method, JocksFunction.class));
        if (strMethod.isPresent()) {
            return strMethod.get().call(new ArrayList<>()).str();
        } else {
            return String.format("Instance(%s)", _class.getIdentifier().getText());
        }
    }

    public Optional<JocksValue> getProperty(String identifier) {
        return Optional.ofNullable(
                _properties.getOrDefault(identifier, null));
    }

    public void setProperty(String identifier, JocksValue value) {
        _properties.put(identifier, value);
    }

    public Optional<JocksValue> getMethod(String identifier) {
        return _class.getMethodRecursive(identifier)
                .map((method) -> new BoundMethod(this, method));
    }

    private final JocksClass _class;
    private final HashMap<String, JocksValue> _properties = new HashMap<>();
}
