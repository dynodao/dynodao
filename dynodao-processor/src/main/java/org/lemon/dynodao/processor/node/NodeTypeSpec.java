package org.lemon.dynodao.processor.node;

import com.squareup.javapoet.TypeSpec;
import lombok.Value;
import org.lemon.dynodao.processor.BuiltTypeSpec;

import javax.lang.model.element.TypeElement;

/**
 * A built model type. Pojo types include the staged builder, index intermediary stages, and the implementations
 * of {@link org.lemon.dynodao.DocumentLoad} and {@link org.lemon.dynodao.DocumentQuery}.
 */
@Value
public class NodeTypeSpec implements BuiltTypeSpec {

    private final NodeClassData node;
    private final TypeSpec typeSpec;

    @Override
    public TypeElement getDocument() {
        return node.getDocument();
    }

}
