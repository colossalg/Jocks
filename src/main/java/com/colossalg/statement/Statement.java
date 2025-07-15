package com.colossalg.statement;

public abstract class Statement {

    Statement(String file, int line) {
        _file = file;
        _line = line;
    }

    public abstract <T> T accept(StatementVisitor<T> visitor);

    public String getFile() {
        return _file;
    }

    public int getLine() {
        return _line;
    }

    private final String _file;
    private final int _line;
}
