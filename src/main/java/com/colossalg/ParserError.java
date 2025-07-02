package com.colossalg;

public class ParserError implements Error {

    public ParserError(String what, Token token) {
        _what = what;
        _token = token;
    }

    @Override
    public String getMessage() {
        return String.format(
                "ParserError (%s:%d): %s",
                _token.getFile(),
                _token.getLine(),
                _what);
    }

    private final String _what;
    private final Token _token;
}
