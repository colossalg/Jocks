package com.colossalg.dataTypes.functions;

import com.colossalg.Token;
import com.colossalg.dataTypes.JocksValue;
import com.colossalg.statement.Statement;
import com.colossalg.visitors.Interpreter;
import com.colossalg.visitors.SymbolTable;

import java.util.List;

public class JocksUserLandFunction extends JocksFunction {

    public JocksUserLandFunction(
            List<Token> parameters,
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
        return _interpreter.executeUserLandFunction(this, arguments);
    }

    public List<Token> getParameters() {
        return _parameters;
    }

    public List<Statement> getStatements() {
        return _statements;
    }

    public SymbolTable getSymbolTable() {
        return _symbolTable;
    }

    // TODO - Revisit this decision.
    //        I'm not 100% sold on my choice here to use the tokens within this class.
    //        Maybe it's better if the runtime representation of functions doesn't
    //        contain details from scanning/parsing, besides the statements which
    //        I don't think there's a way around. It feels that some abstractions
    //        may be leaking into one another.
    //        On the other hand, however, propagating the tokens throughout the code
    //        does make the localization of error messages better as they have the
    //        file and line.
    //        I'm hoping this isn't such an irreversible commitment, but I've
    //        elected to go with it for now, hoping that the benefits outweigh
    //        the costs, and that the fact that tokens are a pretty thin class
    //        mitigates things somewhat.
    private final List<Token> _parameters;
    private final List<Statement> _statements;
    private final SymbolTable _symbolTable;
    private final Interpreter _interpreter;
}
