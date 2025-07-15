package com.colossalg;

import java.util.ArrayList;
import java.util.List;

public class ErrorReporter {

    public void report(JocksError error) {
        _errors.add(error);
    }

    public List<JocksError> getErrors() {
        return _errors;
    }

    private final List<JocksError> _errors = new ArrayList<>();
}
