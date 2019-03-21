package org.dynodao.processor.serialize;

import com.squareup.javapoet.TypeSpec;
import lombok.Builder;
import lombok.Value;
import org.dynodao.processor.BuiltTypeSpec;

import javax.lang.model.element.TypeElement;

/**
 * The built serializer class. Contains all the methods to serialize and deserialize
 * the schema and all attributes within.
 */
@Value
@Builder
public class SerializerTypeSpec implements BuiltTypeSpec {

    private final TypeSpec typeSpec;
    private final TypeElement documentElement;

}
