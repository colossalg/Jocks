package com.colossalg.visitors;

import com.colossalg.dataTypes.JocksValue;

import java.util.HashMap;

public class SymbolTable {

    public SymbolTable(SymbolTable parent, ExceptionFactory exceptionFactory) {
        _parent = parent;
        _exceptionFactory = exceptionFactory;
    }

    public SymbolTable getParent() {
        return _parent;
    }

    public SymbolTable getAncestor(int depth) {
        return depth > 0
                ? _parent.getAncestor(depth - 1)
                : this;
    }

    public void createVariable(String identifier, JocksValue value) {
        if (_variables.containsKey(identifier)) {
            throw _exceptionFactory.createExceptionWithoutFileOrLine(
                    "Attempting to create variable '" + identifier + "' which already exists in scope");
        }
        _variables.put(identifier, value);
    }

    public JocksValue getVariable(String identifier) {
        if (!_variables.containsKey(identifier)) {
            if (_parent == null) {
                throw _exceptionFactory.createExceptionWithoutFileOrLine(
                        "Attempting to get variable '" + identifier + "' which doesn't exist");
            }
            return _parent.getVariable(identifier);
        } else {
            return _variables.get(identifier);
        }
    }

    public void setVariable(String identifier, JocksValue value) {
        if (!_variables.containsKey(identifier)) {
            if (_parent == null) {
                throw _exceptionFactory.createExceptionWithoutFileOrLine(
                        "Attempting to set variable '" + identifier + "' which doesn't exist");
            }
            _parent.setVariable(identifier, value);
        } else {
            _variables.put(identifier, value);
        }
    }

    private final SymbolTable _parent;
    private final ExceptionFactory _exceptionFactory;
    private final HashMap<String, JocksValue> _variables = new HashMap<>();
}
