package com.colossalg;

import com.colossalg.exception.JocksCompileException;
import com.colossalg.statement.Statement;
import com.colossalg.visitors.Interpreter;
import com.colossalg.visitors.PrettyPrinter;

import java.io.IOException;
import java.util.List;

public class Jocks {

    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }

        final var file = args[0];
        final List<Statement> statements;
        try {
            statements = JocksLoader.load(file);
        } catch (IOException exception) {
            System.out.println("ERROR - Couldn't read file.");
            System.out.println(exception.getMessage());
            return;
        } catch (JocksCompileException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        switch (args.length) {
            case 1:
                interpret(file, statements);
                break;
            case 2:
                if (args[1].equals("--print")) {
                    print(statements);
                } else {
                    usage();
                }
                break;
            default:
                usage();
                break;
        }
    }

    private static void interpret(String file, List<Statement> statements) {
        try {
            final var interpreter = new Interpreter(file);
            interpreter.visitAll(statements);
            if (interpreter.getIsThrowing()) {
                System.out.println("ERROR - Program terminating with uncaught thrown value.");
                System.out.println("\tConsider adding a top level try/catch block to log the exception.");
            }
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void print(List<Statement> statements) {
        final var prettyPrinter = new PrettyPrinter();
        System.out.println(prettyPrinter.visitAll(statements));
    }

    private static void usage() {
        System.out.println("USAGE: jocks <source-file-path> [--print]");
        System.out.println("\tsource-file-path - The file path for the source code to interpret or print.");
        System.out.println("\t--print          - If specified, the source code will be pretty-printed.");
    }
}
