package org.lemon.dynodao.processor.context;

import static org.lemon.dynodao.processor.util.ElementUtil.getAnnotationMirrorOfType;

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
import java.util.Optional;

/**
 * A message to display during the compilation phase.
 */
@Data
@Setter(AccessLevel.NONE)
@RequiredArgsConstructor(access = AccessLevel.NONE)
public class ProcessorMessage {

    private final Diagnostic.Kind kind;
    private final String message;

    /**
     * Sole ctor.
     * @param kind the kind of message to send, error, warning etc
     * @param format the string format, used in String#format
     * @param args the args in format
     */
    public ProcessorMessage(Diagnostic.Kind kind, String format, Object... args) {
        this.kind = kind;
        this.message = String.format(format, args);
    }

    private Optional<Element> element = Optional.empty();
    private Optional<AnnotationMirror> annotationMirror = Optional.empty();
    private Optional<AnnotationValue> annotationValue = Optional.empty();

    /**
     * @param element the element this message occurs at
     * @return <tt>this</tt>
     */
    public ProcessorMessage atElement(Element element) {
        this.element = Optional.of(element);
        return this;
    }

    /**
     * @param annotationMirror the annotation this message occurs at
     * @return <tt>this</tt>
     */
    public ProcessorMessage atAnnotation(AnnotationMirror annotationMirror) {
        element.orElseThrow(() -> new IllegalStateException("ProcessorMessage#element must be set in order to set an annotation"));
        this.annotationMirror = Optional.of(annotationMirror);
        return this;
    }

    /**
     * @param annotation the annotation this message occurs at
     * @return <tt>this</tt>
     */
    public ProcessorMessage atAnnotation(Class<? extends Annotation> annotation) {
        element.orElseThrow(() -> new IllegalStateException("ProcessorMessage#element must be set in order to set an annotation"));
        return atAnnotation(getAnnotationMirrorOfType(element.get(), annotation));
    }

    /**
     * @param annotationValue the annotation value this message occurs at
     * @return <tt>this</tt>
     */
    public ProcessorMessage atAnnotationValue(AnnotationValue annotationValue) {
        element.orElseThrow(() -> new IllegalStateException("ProcessorMessage#element must be set in order to set an annotation"));
        annotationMirror.orElseThrow(() -> new IllegalStateException("ProcessorMessage#annotationMirror must be set in order to set an annotation value"));
        this.annotationValue = Optional.of(annotationValue);
        return this;
    }

    /**
     * Submits the message to the annotation processing environment.
     * @param messager the messager to submit to
     */
    public void submit(Messager messager) {
        if (element.isPresent()) {
            if (annotationMirror.isPresent()) {
                if (annotationValue.isPresent()) {
                    messager.printMessage(kind, message, element.get(), annotationMirror.get(), annotationValue.get());
                } else {
                    messager.printMessage(kind, message, element.get(), annotationMirror.get());
                }
            } else {
                messager.printMessage(kind, message, element.get());
            }
        } else {
            messager.printMessage(kind, message);
        }
    }
}
