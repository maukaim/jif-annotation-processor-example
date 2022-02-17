package org.maukaim.jif.example.core;


import org.maukaim.jif.annotation.processor.ParamType;
import org.maukaim.jif.annotation.processor.ProvideConstructor;

import java.util.Map;


@ProvideConstructor(value = @ParamType(String.class))
@ProvideConstructor(value = {
        @ParamType(String.class),
        @ParamType(value = Map.class, genericTypes = {String.class, Integer.class})})
public interface UserService {
    Map<String, Integer> getInventory();
}

