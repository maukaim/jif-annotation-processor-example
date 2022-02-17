package org.maukaim.jif.annotation.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface ConstructorChecker {
    boolean check(ExecutableElement constructor, Parameter[] parametersExpected);

    static ConstructorChecker noArgs() {
        return (constructor, parametersExpected) -> constructor.getParameters().size() == 0;
    }

    static ConstructorChecker ordered() {
        return (constructor, parametersExpected) -> {
            List<? extends VariableElement> parameters = constructor.getParameters();
            if (parameters.size() != parametersExpected.length) {
                return false;
            } else {
                for (int i = 0; i < parametersExpected.length; i++) {
                    VariableElement param = parameters.get(i);
                    Parameter paramExpected = parametersExpected[i];
                    if (!param.asType().toString().equals(HasConstructorUtils.toString(paramExpected))) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    static ConstructorChecker unOrdered() {
        return (constructor, parametersExpected) -> {
            List<? extends VariableElement> parameters = constructor.getParameters();
            if (parameters.size() != parametersExpected.length) {
                return false;
            } else {
                Map<String, Long> expectedParamStats = new HashMap<>();
                for (Parameter parameter : parametersExpected) {
                    String cpString = HasConstructorUtils.toString(parameter);
                    expectedParamStats.compute(cpString, (key, val) -> Objects.requireNonNullElse(val, 0L) + 1);
                }

                Map<String, Long> paramStats = parameters.stream()
                        .collect(Collectors.groupingBy(
                                param -> param.asType().toString(),
                                Collectors.counting()));

                return expectedParamStats.equals(paramStats);
            }
        };

    }

}
