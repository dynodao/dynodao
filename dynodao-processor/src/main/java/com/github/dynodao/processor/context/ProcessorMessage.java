package com.github.dynodao.processor.context;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * A message to display during the compilation phase.
 * <p>
 * Implementation note: this class is mutable, rather than immutable. Immutability can be achieved, but
 * would require interaction with {@link ProcessorMessager} in order to prevent multiple errors being emitted.
 * In favour of keeping both classes simple, this class is mutable.
 */
@Data
@Setter(AccessLevel.NONE)
@RequiredArgsConstructor(access = AccessLevel.NONE)
public final class ProcessorMessage {

    private final Diagnostic.Kind kind;
    private final String message;

    private Element element;
    private AnnotationMirror annotationMirror;
    private AnnotationValue annotationValue;

    /**
     * Sole ctor.
     * @param kind the kind of message to send, error, warning etc
     * @param format the string format, used in String#format
     * @param args the args in format
     * @throws java.util.IllegalFormatException when {@code String.format(format, args)} throws
     */
    ProcessorMessage(Diagnostic.Kind kind, String format, Object... args) {
        this.kind = kind;
        this.message = String.format(format, args);
    }

    /**
     * @param element the element this message occurs at
     * @return <tt>this</tt>
     */
    public ProcessorMessage atElement(Element element) {
        this.element = element;
        return this;
    }

    /**
     * @param annotationMirror the annotation this message occurs at
     * @return <tt>this</tt>
     */
    public ProcessorMessage atAnnotation(AnnotationMirror annotationMirror) {
        Objects.requireNonNull(element, "ProcessorMessage#element must be set in order to set an annotation");
        this.annotationMirror = annotationMirror;
        return this;
    }

    /**
     * @param annotation the annotation this message occurs at
     * @return <tt>this</tt>
     */
    public ProcessorMessage atAnnotation(Class<? extends Annotation> annotation) {
        Objects.requireNonNull(element, "ProcessorMessage#element must be set in order to set an annotation");
        return atAnnotation(getAnnotationMirrorOfType(element, annotation));
    }

    private AnnotationMirror getAnnotationMirrorOfType(Element element, Class<? extends Annotation> annotation) {
        return element.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().asElement().toString().equals(annotation.getCanonicalName()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No such annotation %s found on element %s", annotation, element)));
    }

    /**
     * @param annotationValue the annotation value this message occurs at
     * @return <tt>this</tt>
     */
    public ProcessorMessage atAnnotationValue(AnnotationValue annotationValue) {
        Objects.requireNonNull(element, "ProcessorMessage#element must be set in order to set an annotation value");
        Objects.requireNonNull(annotationMirror, "ProcessorMessage#annotationMirror must be set in order to set an annotation value");
        this.annotationValue = annotationValue;
        return this;
    }

    /**
     * @param attributeName the name of the annotation value this message occurs at
     * @return <tt>this</tt>
     */
    public ProcessorMessage atAnnotationValue(String attributeName) {
        Objects.requireNonNull(element, "ProcessorMessage#element must be set in order to set an annotation value");
        Objects.requireNonNull(annotationMirror, "ProcessorMessage#annotationMirror must be set in order to set an annotation value");
        return atAnnotationValue(getAnnotationValueByName(annotationMirror, attributeName));
    }

    private AnnotationValue getAnnotationValueByName(AnnotationMirror annotation, String attributeName) {
        return annotation.getElementValues().entrySet().stream()
                .filter(entry -> entry.getKey().getSimpleName().contentEquals(attributeName))
                .map(entry -> entry.getValue())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No such method %s() found in %s", attributeName, annotation)));
    }

    /**
     * Submits the message to the annotation processing environment.
     * @param messager the messager to emit to
     */
    void emit(Messager messager) {
        if (element != null) {
            if (annotationMirror != null) {
                if (annotationValue != null) {
                    messager.printMessage(kind, message, element, annotationMirror, annotationValue);
                } else {
                    messager.printMessage(kind, message, element, annotationMirror);
                }
            } else {
                messager.printMessage(kind, message, element);
            }
        } else {
            messager.printMessage(kind, message);
        }
    }

}
