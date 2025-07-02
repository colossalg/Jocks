package com.colossalg.statement;

import com.colossalg.Token;

import java.util.List;
import java.util.Optional;

public class ClassDeclaration implements Statement {

    public ClassDeclaration(Token identifier, Token superClass, List<FunDeclaration> methods) {
        _identifier = identifier;
        _superClass = superClass;
        _methods = methods;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitClassDeclaration(this);
    }

    public Token getIdentifier() {
        return _identifier;
    }

    public Optional<Token> getSuperClass() {
        return Optional.ofNullable(_superClass);
    }

    public List<FunDeclaration> getMethods() {
        return _methods;
    }

    private final Token _identifier;
    private final Token _superClass;
    private final List<FunDeclaration> _methods;
}
