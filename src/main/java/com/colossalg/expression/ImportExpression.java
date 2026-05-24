package com.colossalg.expression;

import com.colossalg.Token;

public class ImportExpression implements Expression {

    public ImportExpression(Token path) {
        _path = path;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitImportExpression(this);
    }

    public Token getPath() {
        return _path;
    }

    private final Token _path;
}
