package com.colossalg.visitors;

import java.util.List;

import java.util.function.Supplier;

// I don't really like this mechanism, but I think it's a necessary evil (for now at least).
// Being able to throw from within the SymbolTable without having to catch it to apply the
// localization from within this class makes the visitor methods below much terser.
// The unfortunate side effect of this is that the SymbolTable's interface relies on Tokens
// rather than Strings for identifiers. This then propagates to JocksClass and
// JocksUserLandFunction who have to store their identifier and parameters
// respectively as Tokens.
// That feels a bit like an abstraction leaking, as Tokens should probably be present during
// the static components of the interpreter (scanning, parsing, resolving), but not so much
// during runtime.
public class ExceptionFactory {

    public ExceptionFactory(Supplier<List<String>> getCallStackEntryInfo) {
        _getCallStackEntryInfo = getCallStackEntryInfo;
    }

    public RuntimeException createException(
            String file,
            int line,
            String format,
            Object... args
    ) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append("An internal error was encountered at runtime.\n");
        stringBuilder.append(String.format(format, args));
        stringBuilder.append('\n');
        stringBuilder.append('\n');
        stringBuilder.append(String.format("This occurred at line %d of file '%s'.\n", line, file));
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
