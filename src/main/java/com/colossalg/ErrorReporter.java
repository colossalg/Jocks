package com.colossalg;

import java.util.ArrayList;
import java.util.List;

public class ErrorReporter {

    public void report(Error error) {
        _errors.add(error);
    }

    public List<Error> getErrors() {
        return _errors;
    }

    private final List<Error> _errors = new ArrayList<>();
}
