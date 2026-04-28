package com.colossalg;

public class ParserException extends JocksCompileException {

    public ParserException(String file, int line, String what) {
        super("Parser", file, line, what);
    }
}
