package org.maukaim.jif.example.core;


import org.maukaim.jif.annotation.processor.ParamType;
import org.maukaim.jif.annotation.processor.ProvideConstructor;

import java.util.List;
import java.util.Map;

@ProvideConstructor(value = {
        @ParamType(String.class),
        @ParamType(value = List.class, genericTypes = Integer.class),

},
        isOrdered = false)
public interface ExampleInterface {

}

