package com.colossalg;

import com.colossalg.statement.Statement;
import com.colossalg.visitors.Resolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class JocksLoader {

    public static List<Statement> load(String filePath) throws IOException {
        return loadInternal(filePath, false);
    }

    public static List<Statement> loadModule(String filePath) throws IOException {
        return loadInternal(filePath, true);
    }

    private static List<Statement> loadInternal(String filePath, boolean isModule) throws IOException {
        final var contents   = readFileContents(filePath);
        final var statements = new Parser(new Scanner(contents, filePath).scanTokens()).parse();
        final var resolver   = new Resolver();
        if (isModule) {
            resolver.pushModuleTopLevelScope();
        }
        resolver.visitAll(statements);
        return statements;
    }

    private static String readFileContents(String filePath) throws IOException {
        final var stringBuilder = new StringBuilder();
        try (final var reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
                line = reader.readLine();
            }
            return stringBuilder.toString();
        }
    }
}
