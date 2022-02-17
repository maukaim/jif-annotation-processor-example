package org.maukaim.jif.example.core;


import org.maukaim.jif.annotation.processor.Parameter;
import org.maukaim.jif.annotation.processor.HasConstructor;

import java.util.Map;


@HasConstructor(value = @Parameter(String.class))
@HasConstructor(value = {
        @Parameter(String.class),
        @Parameter(value = Map.class, genericTypes = {String.class, Integer.class})})
public interface UserService {
    Map<String, Integer> getInventory();
}

