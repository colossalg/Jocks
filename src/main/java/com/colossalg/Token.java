package com.colossalg;

public class Token {

    public Token(TokenType type, Object literal, String text, String file, int line) {
        _type = type;
        _literal = literal;
        _text = text;
        _file = file;
        _line = line;
    }

    @Override
    public String toString() {
        return String.format(
                "Token { type: %s, literal: %s, text: %s, file: %s line: %d }",
                _type,
                _literal,
                _text,
                _file,
                _line);
    }

    public TokenType getType() {
        return _type;
    }

    public Object getLiteral() {
        return _literal;
    }

    public String getText() {
        return _text;
    }

    public String getFile() {
        return _file;
    }

    public int getLine() {
        return _line;
    }

    private final TokenType _type;
    private final Object _literal;
    private final String _text;
    private final String _file;
    private final int _line;
}
