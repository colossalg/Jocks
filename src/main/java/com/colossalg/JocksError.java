package com.colossalg;

public class JocksError {

    public JocksError(String module, String file, int line, String what) {
        _module = module;
        _file = file;
        _line = line;
        _what = what;
    }

    public String getMessage() {
        return String.format("[%s] - (%s:%d) - %s", _module, _file, _line, _what);
    }

    private final String _module;
    private final String _file;
    private final int    _line;
    private final String _what;
}
