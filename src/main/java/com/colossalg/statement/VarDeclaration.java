package com.colossalg.statement;

import com.colossalg.Token;
import com.colossalg.expression.Expression;

public class VarDeclaration extends Statement {

    public VarDeclaration(
            String file,
            int line,
            Token identifier,
            Expression expression
    ) {
        super(file, line);
        _identifier = identifier;
        _expression = expression;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitVarDeclaration(this);
    }

    public Token getIdentifier() {
        return _identifier;
    }

    public Expression getExpression() {
        return _expression;
    }

    private final Token _identifier;
    private final Expression _expression;
}
