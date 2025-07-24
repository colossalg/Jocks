package com.colossalg.visitors;

import com.colossalg.dataTypes.JocksValue;
import com.colossalg.Token;

import java.util.HashMap;

public class SymbolTable {

    public SymbolTable(SymbolTable parent) {
        _parent = parent;
    }

    public SymbolTable getParent() {
        return _parent;
    }

    public SymbolTable getAncestor(int depth) {
        return depth > 0
                ? _parent.getAncestor(depth - 1)
                : this;
    }

    public void createVariable(Token identifier, JocksValue value) {
        if (_variables.containsKey(identifier.getText())) {
            throw ExceptionFactory.createException(
                    identifier.getFile(),
                    identifier.getLine(),
                    "Attempting to create variable '" + identifier.getText() + "' which already exists in scope");
        }
        _variables.put(identifier.getText(), value);
    }

    public JocksValue getVariable(Token identifier) {
        if (!_variables.containsKey(identifier.getText())) {
            if (_parent == null) {
                throw ExceptionFactory.createException(
                        identifier.getFile(),
                        identifier.getLine(),
                        "Attempting to get variable '" + identifier.getText() + "' which doesn't exist");
            }
            return _parent.getVariable(identifier);
        } else {
            return _variables.get(identifier.getText());
        }
    }

    public void setVariable(Token identifier, JocksValue value) {
        if (!_variables.containsKey(identifier.getText())) {
            if (_parent == null) {
                throw ExceptionFactory.createException(
                        identifier.getFile(),
                        identifier.getLine(),
                        "Attempting to set variable '" + identifier.getText() + "' which doesn't exist");
            }
            _parent.setVariable(identifier, value);
        } else {
            _variables.put(identifier.getText(), value);
        }
    }

    private final SymbolTable _parent;
    private final HashMap<String, JocksValue> _variables = new HashMap<>();
}
