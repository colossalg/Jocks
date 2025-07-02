package com.colossalg.dataTypes.functions;

import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.primitives.JocksNil;
import com.colossalg.statement.ReturnStatement;
import com.colossalg.statement.Statement;
import com.colossalg.visitors.Interpreter;
import com.colossalg.visitors.SymbolTable;

import java.util.List;

public class JocksUserLandFunction extends JocksFunction {

    public JocksUserLandFunction(
            List<String> parameters,
            List<Statement> statements,
            SymbolTable symbolTable,
            Interpreter interpreter
    ) {
        _parameters = parameters;
        _statements = statements;
        _symbolTable = symbolTable;
        _interpreter = interpreter;
    }

    @Override
    public int getArity() {
        return _parameters.size();
    }

    @Override
    public JocksValue call(List<JocksValue> arguments) {
        final var oldSymbolTable = _interpreter.getSymbolTable();
        _interpreter.setSymbolTable(new SymbolTable(_symbolTable));

        for (int i = 0; i < _parameters.size(); i++) {
            _interpreter.getSymbolTable().createVariable(_parameters.get(i), arguments.get(i));
        }

        JocksValue result = JocksNil.Instance;
        for (final var statement : _statements) {
            if (statement instanceof ReturnStatement returnStatement) {
                if (returnStatement.getSubExpression().isPresent()) {
                    result = _interpreter.visit(returnStatement.getSubExpression().get());
                }
            } else {
                _interpreter.visit(statement);
            }
        }

        _interpreter.setSymbolTable(oldSymbolTable);

        return result;
    }

    private final List<String> _parameters;
    private final List<Statement> _statements;
    private final SymbolTable _symbolTable;
    private final Interpreter _interpreter;
}
