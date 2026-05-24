package com.colossalg.exception;

public class JocksCompileException extends RuntimeException {

    public JocksCompileException(String module, String file, int line, String what) {
        super(String.format(
                """
                An internal error was encountered in the '%s'.
                Where - (%s:%d)
                What  - %s""",
                module,
                file,
                line,
                what));
    }
}
