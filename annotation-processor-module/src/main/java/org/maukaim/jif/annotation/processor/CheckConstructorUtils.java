package org.maukaim.jif.annotation.processor;

import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CheckConstructorUtils {

    public String paramTypeToString(ParamType paramType) {
        StringBuilder res = new StringBuilder(
                Objects.requireNonNullElse(this.getValueAsTypeMirror(paramType), "")
                        .toString());
        List<? extends TypeMirror> genericsAsTypeMirrors = this.getGenericsAsTypeMirrors(paramType);

        if (!genericsAsTypeMirrors.isEmpty()) {
            String genericMarker = String.format("<%s>",
                    genericsAsTypeMirrors.stream().map(TypeMirror::toString).collect(Collectors.joining(",")));
            res.append(genericMarker);
        }

        return res.toString();
    }

    public boolean checkForConstructorMatching(Element elt, ParamType[] parametersExpected, boolean ordered) {
        return elt.getEnclosedElements().stream()
                .filter(elem -> elem.getKind() == ElementKind.CONSTRUCTOR && elem.getModifiers().contains(Modifier.PUBLIC))
                .map(elem -> (ExecutableElement) elem)
                .anyMatch(constructor -> ordered ?
                        checkConstructorHasOrderedParameters(constructor, parametersExpected) :
                        checkConstructorHasUnorderedParameters(constructor, parametersExpected));
    }

    public boolean checkHasNoArgsConstructor(Element elt) {
        return elt.getEnclosedElements().stream()
                .filter(elem -> elem.getKind() == ElementKind.CONSTRUCTOR && elem.getModifiers().contains(Modifier.PUBLIC))
                .map(elem -> (ExecutableElement) elem)
                .anyMatch(constructor -> constructor.getParameters().size() == 0);
    }

    private boolean checkConstructorHasUnorderedParameters(ExecutableElement constructor, ParamType[] parametersExpected) {
        List<? extends VariableElement> parameters = constructor.getParameters();

        if (parameters.size() != parametersExpected.length) {
            return false;
        }

        Map<String, Long> expectedParamStats = new HashMap<>();
        for (ParamType paramType : parametersExpected) {
            String cpString = this.paramTypeToString(paramType);
            expectedParamStats.compute(cpString, (key, val) -> Objects.requireNonNullElse(val, 0L) + 1);
        }
        Map<String, Long> paramStats = parameters.stream()
                .collect(Collectors.groupingBy(
                        param -> param.asType().toString(),
                        Collectors.counting()));

        return expectedParamStats.equals(paramStats);
    }

    private boolean checkConstructorHasOrderedParameters(ExecutableElement constructor, ParamType[] parametersExpected) {
        List<? extends VariableElement> parameters = constructor.getParameters();

        if (parameters.size() != parametersExpected.length) {
            return false;
        } else {
            for (int i = 0; i < parametersExpected.length; i++) {
                VariableElement param = parameters.get(i);
                ParamType paramExpected = parametersExpected[i];
                if (!param.asType().toString().equals(this.paramTypeToString(paramExpected))) {
                    return false;
                }
            }
            return true;
        }
    }


    private TypeMirror getValueAsTypeMirror(ParamType paramType) {
        try {
            paramType.value();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
        return null;
    }

    private List<? extends TypeMirror> getGenericsAsTypeMirrors(ParamType paramType) {
        try {
            paramType.genericTypes();
        } catch (MirroredTypesException e) {
            return e.getTypeMirrors();
        }

        return List.of();
    }


}
