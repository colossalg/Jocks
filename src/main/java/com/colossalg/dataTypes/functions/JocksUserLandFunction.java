package com.colossalg.dataTypes.functions;

import com.colossalg.dataTypes.JocksValue;
import com.colossalg.statement.Statement;
import com.colossalg.visitors.Interpreter;
import com.colossalg.visitors.SymbolTable;

import java.util.List;

public class JocksUserLandFunction extends JocksFunction {

    public JocksUserLandFunction(
            String name,
            List<String> parameters,
            List<Statement> statements,
            SymbolTable symbolTable,
            Interpreter interpreter
    ) {
        super(name);
        _parameters = parameters;
        _statements = statements;
        _symbolTable = symbolTable;
        _interpreter = interpreter;
    }

    @Override
    public String str() {
        return String.format("JocksUserLandFunction(%s)", getName());
    }

    @Override
    public int getArity() {
        return _parameters.size();
    }

    @Override
    public JocksValue call(List<JocksValue> arguments) {
        return _interpreter.executeUserLandFunction(this, arguments);
    }

    public List<String> getParameters() {
        return _parameters;
    }

    public List<Statement> getStatements() {
        return _statements;
    }

    public SymbolTable getSymbolTable() {
        return _symbolTable;
    }

    private final List<String> _parameters;
    private final List<Statement> _statements;
    private final SymbolTable _symbolTable;
    private final Interpreter _interpreter;
}
