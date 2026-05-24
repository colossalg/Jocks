package com.colossalg.exception;

public class ScannerException extends JocksCompileException {

    public ScannerException(String file, int line, String what) {
        super("Scanner", file, line, what);
    }
}
