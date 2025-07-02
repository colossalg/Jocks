package com.colossalg;

public class ScannerError implements Error {

    public ScannerError(String what, String file, int line) {
        _what = what;
        _file = file;
        _line = line;
    }

    @Override
    public String getMessage() {
        return String.format("ScannerError (%s:%d): %s", _file, _line, _what);
    }

    private final String _what;
    private final String _file;
    private final int _line;
}
