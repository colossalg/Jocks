package com.colossalg;

import com.colossalg.visitors.Interpreter;
import com.colossalg.visitors.PrettyPrinter;
import com.colossalg.visitors.Resolver;

import java.io.*;

public class Jocks {

    public static void main(String[] args) {
        final var errorReporter = new ErrorReporter();

        final var stringBuilder = new StringBuilder();
        try {
            final var reader = new BufferedReader(new FileReader("/home/angus/IdeaProjects/Jocks/test/classes.jocks"));
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
                line = reader.readLine();
            }
        } catch (IOException exception) {
            System.out.println("Couldn't read file.");
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

        System.out.println();
        System.out.println("*".repeat(80));
        System.out.println("Pretty Printing");
        System.out.println("*".repeat(80));
        System.out.println();

        final var prettyPrinter = new PrettyPrinter();
        for (final var statement : statements) {
            System.out.print(prettyPrinter.visit(statement));
        }

        System.out.println();
        System.out.println("*".repeat(80));
        System.out.println("Resolving & Interpreting");
        System.out.println("*".repeat(80));
        System.out.println();

        final var begTime = System.nanoTime();

        final var resolver = new Resolver();
        final var interpreter = new Interpreter();
        for (final var statement : statements) {
            resolver.visit(statement);
            interpreter.visit(statement);
        }

        final var endTime = System.nanoTime();
        final var runTime = (endTime - begTime) / 1_000_000;

        System.out.println();
        System.out.println("*".repeat(80));
        System.out.println("Time");
        System.out.println("*".repeat(80));
        System.out.println();

        System.out.printf("Total run time: %d ms.\n", runTime);
    }
}
