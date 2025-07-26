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
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("An internal error was encountered at runtime (%s, %d).\n", file, line));
        stringBuilder.append(String.format(format, args));
        stringBuilder.append('\n');
        stringBuilder.append('\n');
        stringBuilder.append("Call stack:\n");
        final var callStackEntryInfo = _getCallStackEntryInfo.get();
        for (final var info : callStackEntryInfo) {
            stringBuilder.append('\t');
            stringBuilder.append(info);
            stringBuilder.append('\n');
        }
        return new RuntimeException(stringBuilder.toString());
    }

    public RuntimeException createExceptionWithoutFileOrLine(String format, Object... args) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append("An internal error was encountered at runtime (%s, %d).\n");
        stringBuilder.append(String.format(format, args));
        stringBuilder.append('\n');
        stringBuilder.append('\n');
        stringBuilder.append("Call stack:\n");
        final var callStackEntryInfo = _getCallStackEntryInfo.get();
        for (final var info : callStackEntryInfo) {
            stringBuilder.append('\t');
            stringBuilder.append(info);
            stringBuilder.append('\n');
        }
        return new RuntimeException(stringBuilder.toString());
    }

    private final Supplier<List<String>> _getCallStackEntryInfo;
}
