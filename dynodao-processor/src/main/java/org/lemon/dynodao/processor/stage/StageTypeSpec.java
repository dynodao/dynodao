package org.lemon.dynodao.processor.stage;

import com.squareup.javapoet.TypeSpec;
import lombok.Value;
import org.lemon.dynodao.processor.BuiltTypeSpec;

import javax.lang.model.element.TypeElement;

/**
 * A built model type. Pojo types include the staged builder, index intermediary stages, and the implementations
 * of {@link org.lemon.dynodao.DynoDaoLoad} and {@link org.lemon.dynodao.DynoDaoQuery}.
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
