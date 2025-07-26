package com.colossalg.visitors;

import java.util.List;

import java.util.function.Supplier;

public class ExceptionFactory {

    public ExceptionFactory(Supplier<List<String>> getCallStackEntryInfo) {
        _getCallStackEntryInfo = getCallStackEntryInfo;
    }

    public RuntimeException createExceptionWithFileAndLine(
            String file,
            int line,
            String format,
            Object... args
    ) {
        final var message = String.format(format, args) + "\n\n"
                + String.format("\tAn internal runtime error was encountered (at line %d of file '%s').\n", line, file)
                + getCallStackEntryInfoString();
        return new RuntimeException(message);
    }

    public RuntimeException createExceptionWithoutFileOrLine(String format, Object... args) {
        final var message = String.format(format, args) + "\n\n"
                + "\tAn internal runtime error was encountered.\n"
                + getCallStackEntryInfoString();
        return new RuntimeException(message);
    }

    private String getCallStackEntryInfoString() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append("\tCall stack:\n");
        for (final var info : _getCallStackEntryInfo.get()) {
            stringBuilder.append('\t');
            stringBuilder.append('\t');
            stringBuilder.append(info);
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    private final Supplier<List<String>> _getCallStackEntryInfo;
}
