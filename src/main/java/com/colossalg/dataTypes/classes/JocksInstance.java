package com.colossalg.dataTypes.classes;

import com.colossalg.TokenType;
import com.colossalg.builtin.functions.BoundMethod;
import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class JocksInstance extends JocksValue {

    public static String binaryOperatorTypeToSourceString(TokenType operator) {
        return switch (operator) {
            case TokenType.EQUAL_EQUAL -> "==";
            case TokenType.BANGS_EQUAL -> "!=";
            case TokenType.LESS_THAN -> "<";
            case TokenType.LESS_THAN_OR_EQUAL -> "<=";
            case TokenType.MORE_THAN -> ">";
            case TokenType.MORE_THAN_OR_EQUAL -> ">=";
            case TokenType.ADD -> "+";
            case TokenType.SUB -> "-";
            case TokenType.MUL -> "*";
            case TokenType.DIV -> "/";
            default -> "UNKNOWN OPERATOR";
        };
    }

    public static String unaryOperatorTypeToSourceString(TokenType operator) {
        return switch (operator) {
            case TokenType.ADD -> "+";
            case TokenType.SUB -> "-";
            default -> "UNKNOWN OPERATOR";
        };
    }

    public static String binaryOperatorTypeToMethodString(TokenType operator) {
        return switch (operator) {
            case TokenType.EQUAL_EQUAL -> "__equal__";
            case TokenType.BANGS_EQUAL -> "__not_equal__";
            case TokenType.LESS_THAN -> "__less_than__";
            case TokenType.LESS_THAN_OR_EQUAL -> "__less_than_or_equal__";
            case TokenType.MORE_THAN -> "__more_than__";
            case TokenType.MORE_THAN_OR_EQUAL -> "__more_than_or_equal__";
            case TokenType.ADD -> "__add__";
            case TokenType.SUB -> "__sub__";
            case TokenType.MUL -> "__mul__";
            case TokenType.DIV -> "__div__";
            default -> "UNKNOWN BINARY OPERATOR";
        };
    }

    public static String unaryOperatorTypeToMethodString(TokenType operator) {
        return switch (operator) {
            case TokenType.ADD -> "__unary_add__";
            case TokenType.SUB -> "__unary_sub__";
            default -> "UNKNOWN UNARY OPERATOR";
        };
    }

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
        return executeOverloadedBinaryOperator(TokenType.EQUAL_EQUAL, other);
    }

    @Override
    public JocksValue notEqual(JocksValue other) {
        return executeOverloadedBinaryOperator(TokenType.BANGS_EQUAL, other);
    }

    @Override
    public JocksValue lessThan(JocksValue other) {
        return executeOverloadedBinaryOperator(TokenType.LESS_THAN, other);
    }

    @Override
    public JocksValue lessThanOrEqual(JocksValue other) {
        return executeOverloadedBinaryOperator(TokenType.LESS_THAN_OR_EQUAL, other);
    }

    @Override
    public JocksValue moreThan(JocksValue other) {
        return executeOverloadedBinaryOperator(TokenType.MORE_THAN, other);
    }

    @Override
    public JocksValue moreThanOrEqual(JocksValue other) {
        return executeOverloadedBinaryOperator(TokenType.MORE_THAN_OR_EQUAL, other);
    }

    @Override
    public JocksValue add() {
        return executeOverloadedUnaryOperator(TokenType.ADD);
    }

    @Override
    public JocksValue add(JocksValue other) {
        return executeOverloadedBinaryOperator(TokenType.ADD, other);
    }

    @Override
    public JocksValue sub() {
        return executeOverloadedUnaryOperator(TokenType.SUB);
    }

    @Override
    public JocksValue sub(JocksValue other) {
        return executeOverloadedBinaryOperator(TokenType.SUB, other);
    }

    @Override
    public JocksValue mul(JocksValue other) {
        return executeOverloadedBinaryOperator(TokenType.MUL, other);
    }

    @Override
    public JocksValue div(JocksValue other) {
        return executeOverloadedBinaryOperator(TokenType.DIV, other);
    }

    public JocksClass getJClass() {
        return _class;
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

    private JocksValue executeOverloadedBinaryOperator(TokenType operator, JocksValue other) {
        final var method = getMethod(binaryOperatorTypeToMethodString(operator));
        if (method.isPresent()) {
            final var args = new ArrayList<JocksValue>() {{ add(other); }};
            return method.get().call(args);
        } else {
            throw new UnsupportedOperationException(
                    String.format(
                            "The '%s' operator has not been overridden for the class '%s' or any of its super classes.\n" +
                                    "\tConsider implementing the '%s' method to fix this error.",
                            binaryOperatorTypeToSourceString(operator),
                            _class.getIdentifier().getText(),
                            binaryOperatorTypeToMethodString(operator)));
        }
    }

    private JocksValue executeOverloadedUnaryOperator(TokenType operator) {
        final var method = getMethod(unaryOperatorTypeToMethodString(operator));
        if (method.isPresent()) {
            return method.get().call(new ArrayList<>());
        } else {
            throw new UnsupportedOperationException(
                    String.format(
                            "The '%s' operator has not been overridden for the class '%s' or any of its super classes.\n" +
                                    "\tConsider implementing the '%s' method to fix this error.",
                            unaryOperatorTypeToSourceString(operator),
                            _class.getIdentifier().getText(),
                            unaryOperatorTypeToMethodString(operator)));
        }
    }

    private final JocksClass _class;
    private final HashMap<String, JocksValue> _properties = new HashMap<>();
}
