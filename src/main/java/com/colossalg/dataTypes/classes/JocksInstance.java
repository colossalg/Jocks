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
        return executeOverloadedBinaryOperator("==", "__equal__", other);
    }

    @Override
    public JocksValue notEqual(JocksValue other) {
        return executeOverloadedBinaryOperator("!=", "__not_equal__", other);
    }

    @Override
    public JocksValue lessThan(JocksValue other) {
        return executeOverloadedBinaryOperator("<", "__less_than__", other);
    }

    @Override
    public JocksValue lessThanOrEqual(JocksValue other) {
        return executeOverloadedBinaryOperator("<=", "__less_than_or_equal__", other);
    }

    @Override
    public JocksValue moreThan(JocksValue other) {
        return executeOverloadedBinaryOperator(">", "__more_than__", other);
    }

    @Override
    public JocksValue moreThanOrEqual(JocksValue other) {
        return executeOverloadedBinaryOperator(">=", "__more_than_or_equal__", other);
    }

    @Override
    public JocksValue add() {
        return executeOverloadedUnaryOperator("+", "__unary_add__");
    }

    @Override
    public JocksValue add(JocksValue other) {
        return executeOverloadedBinaryOperator("+", "__add__", other);
    }

    @Override
    public JocksValue sub() {
        return executeOverloadedUnaryOperator("-", "__unary_sub__");
    }

    @Override
    public JocksValue sub(JocksValue other) {
        return executeOverloadedBinaryOperator("-", "__sub__", other);
    }

    @Override
    public JocksValue mul(JocksValue other) {
        return executeOverloadedBinaryOperator("*", "__mul__", other);
    }

    @Override
    public JocksValue div(JocksValue other) {
        return executeOverloadedBinaryOperator("/", "__div__", other);
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

    private JocksValue executeOverloadedBinaryOperator(String operator, String methodName, JocksValue other) {
        final var method = getMethod(methodName);
        if (method.isPresent()) {
            final var args = new ArrayList<JocksValue>() {{ add(other); }};
            return method.get().call(args);
        } else {
            throw new UnsupportedOperationException(
                    String.format(
                            "The '%s' operator has not been overridden for the class '%s' or any of its super classes.\n" +
                                    "\tConsider implementing the '%s' method to fix this error.",
                            operator,
                            _class.getIdentifier().getText(),
                            methodName));
        }
    }

    private JocksValue executeOverloadedUnaryOperator(String operator, String methodName) {
        final var method = getMethod(methodName);
        if (method.isPresent()) {
            return method.get().call(new ArrayList<>());
        } else {
            throw new UnsupportedOperationException(
                    String.format(
                            "The '%s' operator has not been overridden for the class '%s' or any of its super classes.\n" +
                                    "\tConsider implementing the '%s' method to fix this error.",
                            operator,
                            _class.getIdentifier().getText(),
                            methodName));
        }
    }

    private final JocksClass _class;
    private final HashMap<String, JocksValue> _properties = new HashMap<>();
}
