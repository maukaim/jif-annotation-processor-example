package org.maukaim.jif.example.core;


import org.maukaim.jif.annotation.processor.ParamType;
import org.maukaim.jif.annotation.processor.ProvideConstructor;

import java.util.List;

@ProvideConstructor(value = {
        @ParamType(value = List.class, genericType = String.class),
        @ParamType(String.class)
},
        isOrdered = true)
public interface ExampleInterface {

}
