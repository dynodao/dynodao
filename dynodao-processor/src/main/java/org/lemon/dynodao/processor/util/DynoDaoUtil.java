package org.lemon.dynodao.processor.util;

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
            .addMember("value", "$S", "org.lemon.dynodao.processor")
            .addMember("date", "$S", ZonedDateTime.now())
            .addMember("comments", "$S", "https://github.com/twentylemon/dynodao")
            .build();

    /**
     * Returns the {@link Generated} annotation every class should have.
     * @return the {@link Generated} annotation to attach to generated classes
     */
    public static AnnotationSpec generatedAnnotation() {
        return GENERATED_ANNOTATION;
    }

}
