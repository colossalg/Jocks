package com.colossalg.dataTypes.classes;

import com.colossalg.Token;
import com.colossalg.builtin.functions.NoopConstructor;
import com.colossalg.dataTypes.JocksValue;
import com.colossalg.dataTypes.functions.JocksFunction;

import java.util.HashMap;
import java.util.Optional;

public class JocksClass extends JocksValue {

    public JocksClass(Token identifier, JocksClass superClass, HashMap<String, JocksFunction> methods) {
        _identifier = identifier;
        _superClass = superClass;
        _methods = methods;

        if (getMethod("__init__").isEmpty()) {
            _methods.put("__init__", new NoopConstructor());
        }
    }

    @Override
    public String str() {
        return String.format("Class(%s)", _identifier.getText());
    }

    public JocksInstance createInstance() {
        return new JocksInstance(this);
    }

    public Token getIdentifier() {
        return _identifier;
    }

    public Optional<JocksFunction> getMethod(String identifier) {
        return Optional.ofNullable(
                _methods.getOrDefault(identifier, null));
    }

    public Optional<JocksFunction> getMethodRecursive(String identifier) {
        return getMethod(identifier)
                .or(() -> _superClass == null
                    ? Optional.empty()
                    : _superClass.getMethodRecursive(identifier));
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
    private final JocksClass _superClass;
    private final HashMap<String, JocksFunction> _methods;
}
