package com.colossalg;

import com.colossalg.statement.Statement;
import com.colossalg.visitors.Interpreter;
import com.colossalg.visitors.PrettyPrinter;
import com.colossalg.visitors.Resolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Jocks {

    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }

        final var file = args[0];
        String fileContents;
        try {
            fileContents = readFileContents(file);
        } catch (IOException exception) {
            System.out.println("ERROR - Couldn't read file.");
            System.out.println(exception.getMessage());
            return;
        }

        final var errorReporter = new ErrorReporter();

        final var scanner = new Scanner(errorReporter, fileContents, file);
        final var tokens  = scanner.scanTokens();
        if (!errorReporter.getErrors().isEmpty()) {
            for (final var error : errorReporter.getErrors()) {
                System.out.println(error.getMessage());
            }
            return;
        }

        final var parser = new Parser(errorReporter, tokens);
        final var statements = parser.parse();
        if (!errorReporter.getErrors().isEmpty()) {
            for (final var error : errorReporter.getErrors()) {
                System.out.println(error.getMessage());
            }
            return;
        }

        final var resolver = new Resolver(errorReporter);
        resolver.visitAll(statements);
        if (!errorReporter.getErrors().isEmpty()) {
            for (final var error : errorReporter.getErrors()) {
                System.out.println(error.getMessage());
            }
            return;
        }

        switch (args.length) {
            case 1:
                interpret(statements);
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

    private static String readFileContents(String file) throws IOException {
        final var stringBuilder = new StringBuilder();
        final var reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            stringBuilder.append('\n');
            line = reader.readLine();
        }
        return stringBuilder.toString();
    }

    private static void interpret(List<Statement> statements) {
        try {
            final var interpreter = new Interpreter();
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
