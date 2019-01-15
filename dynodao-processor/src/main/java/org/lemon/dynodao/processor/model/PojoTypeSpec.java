package org.lemon.dynodao.processor.model;

import com.squareup.javapoet.TypeSpec;

import lombok.Value;

/**
 * A built pojo type.
 */
@Value
public class PojoTypeSpec {

    private final PojoClassBuilder pojo;
    private final TypeSpec typeSpec;

}
