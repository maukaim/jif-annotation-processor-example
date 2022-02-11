package org.maukaim.jif.example.core;

import org.maukaim.jif.annotation.processor.ProvideConstructor;
import org.maukaim.jif.annotation.processor.ParamType;

import java.util.List;
import java.util.Map;


public class ExampleClass implements ExampleInterface {
    private final String exampleStringField;
    private final List<String> exampleGenericField;


    public ExampleClass(List<String> list, String str) {
        this.exampleStringField = str;
        this.exampleGenericField = list;
    }
}
