package org.lemon.dynodao.processor.schema;

import lombok.Builder;
import lombok.Value;
import org.lemon.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import org.lemon.dynodao.processor.schema.index.DynamoIndex;

import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * The structured schema depicted by an annotated document class.
 */
@Value
@Builder
public class DynamoSchema {

    private final String tableName;
    private final DocumentDynamoAttribute document;
    private final Set<DynamoIndex> indexes;

    /**
     * @return the {@link TypeElement} which this schema is modelled after
     */
    public TypeElement getDocumentElement() {
        return (TypeElement) document.getElement();
    }

}
