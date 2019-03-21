package org.dynodao.processor.stage;

import com.squareup.javapoet.TypeSpec;
import lombok.Value;
import org.dynodao.DynoDaoLoad;
import org.dynodao.DynoDaoQuery;
import org.dynodao.processor.BuiltTypeSpec;

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
