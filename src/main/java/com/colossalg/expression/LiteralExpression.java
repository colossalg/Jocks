package com.colossalg.expression;

import com.colossalg.Token;

public class LiteralExpression extends Expression {

    public LiteralExpression(
            String file,
            int line,
            Token token
    ) {
        super(file, line);
        _token = token;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitLiteralExpression(this);
    }

    public Token getToken() {
        return _token;
    }

    private final Token _token;
}
