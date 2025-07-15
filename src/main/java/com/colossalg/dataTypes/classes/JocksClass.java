package com.colossalg.dataTypes.classes;

import com.colossalg.Token;
import com.colossalg.builtin.functions.NoopConstructor;
import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksFunction;

import java.util.HashMap;
import java.util.Optional;

public class JocksClass extends JocksValue {

    public JocksClass(Token identifier, Token superClass, HashMap<String, JocksFunction> methods) {
        _identifier = identifier;
        _superClass = superClass;
        _methods = methods;

        if (getMethod("__init__").isEmpty()) {
            _methods.put("__init__", new NoopConstructor());
        }
    }

    public JocksInstance createInstance() {
        return new JocksInstance(_identifier);
    }

    public Token getIdentifier() {
        return _identifier;
    }

    public Token getSuperClass() {
        return _superClass;
    }

    public HashMap<String, JocksFunction> getMethods() {
        return _methods;
    }

    public Optional<JocksFunction> getMethod(String identifier) {
        return Optional.ofNullable(
                _methods.getOrDefault(identifier, null));
    }

    // TODO - Revisit this decision.
    //        I'm not 100% sold on my choice here to use the tokens within this class.
    //        Maybe it's better if the runtime representation of classes doesn't
    //        contain details from scanning/parsing. It feels that some abstractions
    //        may be leaking into one another.
    //        On the other hand, however, propagating the tokens throughout the code
    //        does make the localization of error messages better as they have the
    //        file and line.
    //        I'm hoping this isn't such an irreversible commitment, but I've
    //        elected to go with it for now, hoping that the benefits outweigh
    //        the costs, and that the fact that tokens are a pretty thin class
    //        mitigates things somewhat.
    private final Token _identifier;
    private final Token _superClass;
    private final HashMap<String, JocksFunction> _methods;
}
