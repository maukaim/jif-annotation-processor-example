package org.maukaim.jif.annotation.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


@AutoService(Processor.class)
public class HasConstructorProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {

        return Set.of(HasConstructor.class.getCanonicalName(),
                HasConstructors.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedTypes = roundEnv.getElementsAnnotatedWith(annotation);

            for (Element annotatedType : annotatedTypes) {

                HasConstructor[] hasConstructors = annotatedType.getAnnotationsByType(HasConstructor.class);
                List<HasConstructor> hasConstructorList = Arrays.stream(hasConstructors).toList();

                if (annotatedType.getKind().isClass() && !annotatedType.getModifiers().contains(Modifier.ABSTRACT)) {
                    hasConstructorList.forEach(annInstance -> this.processElement(annotatedType, annInstance));
                }

                TypeMirror annEltAsType = annotatedType.asType();
                roundEnv.getRootElements().stream()
                        .filter(elt -> elt.getKind().isClass() &&
                                       !elt.getModifiers().contains(Modifier.ABSTRACT) &&
                                       processingEnv.getTypeUtils().isAssignable(elt.asType(), annEltAsType) &&
                                       !elt.equals(annotatedType))
                        .forEach(elt -> hasConstructorList.forEach(annInstance ->
                                this.processElement(elt, annInstance)));

            }
        }
        return false;
    }


    private void processElement(Element elt, HasConstructor hasConstructorAnnotation) {
        Parameter[] parametersExpected = hasConstructorAnnotation.value();

        ConstructorChecker checker = parametersExpected.length == 0 ?
                ConstructorChecker.noArgs() : hasConstructorAnnotation.isOrdered() ?
                ConstructorChecker.ordered() : ConstructorChecker.unOrdered();

        boolean aConstructorMatches = elt.getEnclosedElements().stream()
                .filter(HasConstructorUtils::isPublicConstructor)
                .map(elem -> (ExecutableElement) elem)
                .anyMatch(constructor -> checker.check(constructor, parametersExpected));

        if (!aConstructorMatches) {
            String message = HasConstructorUtils.buildErrorMessage(
                    elt.getSimpleName(), hasConstructorAnnotation.isOrdered(), parametersExpected);

            this.processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, message, elt);
        }
    }

}
