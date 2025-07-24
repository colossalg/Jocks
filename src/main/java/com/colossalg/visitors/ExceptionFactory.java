package com.colossalg.visitors;

import com.colossalg.expression.Expression;
import com.colossalg.statement.Statement;

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

    public static RuntimeException createException(
            String file,
            int line,
            String format,
            Object... args
    ) {
        final var what = String.format(format, args);
        final var message = String.format(
                """
                An internal error was encountered at runtime.
                Where - (%s:%d)
                What  - %s""",
                file,
                line,
                what);
        return new RuntimeException(message);
    }
}
