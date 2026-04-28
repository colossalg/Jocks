package com.colossalg;

public class ResolverException extends JocksCompileException {

    public ResolverException(String file, int line, String what) {
        super("Resolver", file, line, what);
    }
}
