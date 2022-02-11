package org.maukaim.jif.annotation.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * TODO: Refactor the whole system
 * - 1 Strategy Pattern pour les 3 case de constructor check
 * - CheckConstructorUtils should be static?
 * - Comments should only exist if error! Refacto comments following way:
 * - X implements Y. Constructor Check required:
 * - expected: ...... (or "no-args")
 * - actual: ...... (or "n" constructors with parameters.)
 * - Please provide the expected constructor.
 */
@AutoService(Processor.class)
public class CheckConstructorProcessor extends AbstractProcessor {

    private CheckConstructorUtils checkConstructorUtils;


    @Override
    public Set<String> getSupportedAnnotationTypes() {

        return Set.of(ProvideConstructor.class.getCanonicalName());
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.checkConstructorUtils = new CheckConstructorUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedTypes = roundEnv.getElementsAnnotatedWith(annotation);

            for (Element annotatedType : annotatedTypes) {
                ProvideConstructor annotationInstance = annotatedType.getAnnotation(ProvideConstructor.class);

                if (annotatedType.getKind().isClass() && !annotatedType.getModifiers().contains(Modifier.ABSTRACT)) {
                    this.processElement(annotatedType, annotationInstance);
                }


                TypeMirror annEltAsType = annotatedType.asType();
                roundEnv.getRootElements().stream()
                        .filter(elt -> elt.getKind().isClass() &&
                                !elt.getModifiers().contains(Modifier.ABSTRACT) &&
                                processingEnv.getTypeUtils().isAssignable(elt.asType(), annEltAsType) &&
                                !elt.equals(annotatedType))
                        .forEach(elt -> this.processElement(elt, annotationInstance));
            }
        }
        return false;
    }


    private void processElement(Element elt, ProvideConstructor provideConstructorAnnotation) {
        ParamType[] parametersExpected = provideConstructorAnnotation.value();

        boolean aConstructorMatches;
        StringBuilder resultMessage = new StringBuilder(
                String.format("Constructor check for %s -> ", elt.getSimpleName()));

        if (parametersExpected.length == 0) {
            aConstructorMatches = this.checkConstructorUtils.checkHasNoArgsConstructor(elt);
            resultMessage.append(aConstructorMatches ?
                    " has a no-args constructor as expected." : "does not have the expected no-args constructor.");

        } else {
            aConstructorMatches = this.checkConstructorUtils.checkForConstructorMatching(elt,
                    parametersExpected, provideConstructorAnnotation.isOrdered());
            resultMessage.append(aConstructorMatches ? "has" : "does not have");
            resultMessage.append(String.format(" the expected constructor with args (%s) : ",
                    provideConstructorAnnotation.isOrdered() ? "in this order" : "with no order"));
            resultMessage.append(Stream.of(parametersExpected).map(this.checkConstructorUtils::paramTypeToString)
                    .collect(Collectors.joining(", ")));
        }

        Diagnostic.Kind diagnosticKind = aConstructorMatches ? Diagnostic.Kind.NOTE : Diagnostic.Kind.ERROR;
        this.processingEnv.getMessager()
                .printMessage(diagnosticKind, resultMessage.toString(), elt);
    }

}
