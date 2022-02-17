package org.maukaim.jif.annotation.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HasConstructorUtils {
    public static final String STD_GENERIC_FORMAT = "<%s>";
    public static final String STD_ERROR_MESSAGE_FORMAT = "FAILED Constructor Check for %s, expected %s.";


    public static String toString(Parameter parameter) {
        StringBuilder res = new StringBuilder(
                Objects.requireNonNullElse(getValueAsTypeMirror(parameter), "")
                        .toString());
        List<? extends TypeMirror> genericsAsTypeMirrors = getGenericsAsTypeMirrors(parameter);

        if (!genericsAsTypeMirrors.isEmpty()) {
            String genericMarker = String.format(STD_GENERIC_FORMAT,
                    genericsAsTypeMirrors.stream().map(TypeMirror::toString).collect(Collectors.joining(",")));
            res.append(genericMarker);
        }

        return res.toString();
    }

    private static TypeMirror getValueAsTypeMirror(Parameter parameter) {
        try {
            parameter.value();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
        return null;
    }

    private static List<? extends TypeMirror> getGenericsAsTypeMirrors(Parameter parameter) {
        try {
            parameter.genericTypes();
        } catch (MirroredTypesException e) {
            return e.getTypeMirrors();
        }

        return List.of();
    }


    public static boolean isPublicConstructor(Element elem) {
        return elem.getKind() == ElementKind.CONSTRUCTOR && elem.getModifiers().contains(Modifier.PUBLIC);
    }

    public static String buildErrorMessage(Name eltName, boolean ordered, Parameter[] parametersExpected) {
        String expectedValue = parametersExpected.length == 0 ?
                "No-Args constructor" : String.format("constructor with (%s) args: %s",
                ordered ? "ordered" : "not ordered",
                Stream.of(parametersExpected)
                        .map(HasConstructorUtils::toString)
                        .collect(Collectors.joining(", ")));
        return String.format(HasConstructorUtils.STD_ERROR_MESSAGE_FORMAT, eltName, expectedValue);
    }
}
