package com.colossalg.dataTypes.classes;

import com.colossalg.Token;
import com.colossalg.dataTypes.JocksValue;

import java.util.HashMap;
import java.util.Optional;

public class JocksInstance extends JocksValue {

    public JocksInstance(Token className) {
        _className = className;
    }

    public Token getClassName() {
        return _className;
    }

    public Optional<JocksValue> getProperty(String identifier) {
        return Optional.ofNullable(
                _properties.getOrDefault(identifier, null));
    }

    public void setProperty(String identifier, JocksValue value) {
        _properties.put(identifier, value);
    }

    // TODO - Revisit this decision.
    //        I'm not 100% sold on my choice here to use the tokens within this class.
    //        Maybe it's better if the runtime representation of instances doesn't
    //        contain details from scanning/parsing. It feels that some abstractions
    //        may be leaking into one another.
    //        On the other hand, however, propagating the tokens throughout the code
    //        does make the localization of error messages better as they have the
    //        file and line.
    //        I'm hoping this isn't such an irreversible commitment, but I've
    //        elected to go with it for now, hoping that the benefits outweigh
    //        the costs, and that the fact that tokens are a pretty thin class
    //        mitigates things somewhat.
    private final Token _className;
    private final HashMap<String, JocksValue> _properties = new HashMap<>();
}
