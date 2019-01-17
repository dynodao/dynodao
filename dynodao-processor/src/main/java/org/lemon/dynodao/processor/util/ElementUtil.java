package org.lemon.dynodao.processor.util;

import lombok.experimental.UtilityClass;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

/**
 * Utility methods for {@link Element} and {@link javax.lang.model.type.TypeMirror} types.
 */
@UtilityClass
public class ElementUtil {

    /**
     * Returns the annotation mirror of the specified class.
     * @param element the element which has the annotation
     * @param annotation the annotation class to get the mirror of
     * @return the annotation mirror, or null if none exists
     */
    public static AnnotationMirror getAnnotationMirrorOfType(Element element, Class<? extends Annotation> annotation) {
        return element.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().asElement().toString().equals(annotation.getCanonicalName()))
                .findAny().orElse(null);
    }
}
