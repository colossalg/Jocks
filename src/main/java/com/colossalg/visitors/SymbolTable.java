package com.colossalg.visitors;

import com.colossalg.dataTypes.JocksValue;

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

    public void createVariable(String identifier, JocksValue value) {
        if (_variables.containsKey(identifier)) {
            // TODO - Need to suss out how to actually handle run time errors.
            //        Need a way to throw/catch errors within the language itself.
            throw new IllegalStateException("Attempting to create local variable which already exists.");
        }
        _variables.put(identifier, value);
    }

    public JocksValue getVariable(String identifier) {
        if (!_variables.containsKey(identifier)) {
            if (_parent == null) {
                // TODO - Need to suss out how to actually handle run time errors.
                //        Need a way to throw/catch errors within the language itself.
                throw new IllegalStateException("Attempting to get local variable which does not exist.");
            }
            return _parent.getVariable(identifier);
        } else {
            return _variables.get(identifier);
        }
    }

    public void setVariable(String identifier, JocksValue value) {
        if (!_variables.containsKey(identifier)) {
            if (_parent == null) {
                // TODO - Need to suss out how to actually handle run time errors.
                //        Need a way to throw/catch errors within the language itself.
                throw new IllegalStateException("Attempting to set local variable which does not exist.");
            }
            _parent.setVariable(identifier, value);
        } else {
            _variables.put(identifier, value);
        }
    }

    private final SymbolTable _parent;
    private final HashMap<String, JocksValue> _variables = new HashMap<>();
}
