package com.colossalg;

import com.colossalg.visitors.Interpreter;
import com.colossalg.visitors.Resolver;

import java.io.*;

public class Jocks {

    public static void main(String[] args) {
        final var errorReporter = new ErrorReporter();

        final var stringBuilder = new StringBuilder();
        try {
            final var reader = new BufferedReader(new FileReader(args[0]));
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
                line = reader.readLine();
            }
        } catch (IOException exception) {
            System.out.println("ERROR - Couldn't read file.");
            return;
        }

        final var scanner = new Scanner(errorReporter, stringBuilder.toString());
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

        try {
            final var interpreter = new Interpreter();
            interpreter.visitAll(statements);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
