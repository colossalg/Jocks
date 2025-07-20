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
        final var strMethod = getMethod("__str__");
        if (strMethod.isPresent()) {
            return strMethod.get().call(new ArrayList<>()).str();
        } else {
            return String.format("Instance(%s)", _class.getIdentifier().getText());
        }
    }

    @Override
    public JocksValue equal(JocksValue other) {
        final var equalMethod = getMethod("__equal__");
        if (equalMethod.isPresent()) {
            final var args = new ArrayList<JocksValue>() {{ add(other); }};
            return equalMethod.get().call(args);
        } else {
            throw new UnsupportedOperationException(
                    String.format(
                            "The '==' operator has not been overridden for the class '%s' or any of its super classes.\n" +
                            "\tConsider implementing the '__equal__' method to fix this error.",
                            _class.getIdentifier().getText()));
        }
    }

    public Optional<JocksValue> getProperty(String identifier) {
        return Optional.ofNullable(
                _properties.getOrDefault(identifier, null));
    }

    public void setProperty(String identifier, JocksValue value) {
        _properties.put(identifier, value);
    }

    public Optional<JocksFunction> getMethod(String identifier) {
        return _class.getMethodRecursive(identifier)
                .map((method) -> new BoundMethod(this, method));
    }

    private final JocksClass _class;
    private final HashMap<String, JocksValue> _properties = new HashMap<>();
}
