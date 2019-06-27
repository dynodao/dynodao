package com.github.dynodao.processor.util;

import com.squareup.javapoet.AnnotationSpec;
import lombok.experimental.UtilityClass;

import javax.annotation.Generated;
import java.time.ZonedDateTime;

/**
 * Utility methods specific to dynodao.
 */
@UtilityClass
public class DynoDaoUtil {

    private static final AnnotationSpec GENERATED_ANNOTATION = AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", "com.github.dynodao.processor")
            .addMember("date", "$S", ZonedDateTime.now())
            .addMember("comments", "$S", "https://github.com/dynodao/dynodao")
            .build();

    /**
     * Returns the {@link Generated} annotation every class should have.
     * @return the {@link Generated} annotation to attach to generated classes
     */
    public static AnnotationSpec generatedAnnotation() {
        return GENERATED_ANNOTATION;
    }

}
