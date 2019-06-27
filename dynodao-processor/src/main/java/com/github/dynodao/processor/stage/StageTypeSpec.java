package com.github.dynodao.processor.stage;

import com.github.dynodao.DynoDaoLoad;
import com.github.dynodao.DynoDaoQuery;
import com.github.dynodao.processor.BuiltTypeSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Value;

import javax.lang.model.element.TypeElement;

/**
 * A built model type. Pojo types include the staged builder, index intermediary stages, and the implementations
 * of {@link DynoDaoLoad} and {@link DynoDaoQuery}.
 */
@Value
public class StageTypeSpec implements BuiltTypeSpec {

    private final Stage stage;
    private final TypeSpec typeSpec;

    @Override
    public TypeElement getDocumentElement() {
        return stage.getDocumentElement();
    }

}
