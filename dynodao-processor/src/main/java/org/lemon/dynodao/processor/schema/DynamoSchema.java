package org.lemon.dynodao.processor.schema;

import lombok.Builder;
import lombok.Value;
import org.lemon.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import org.lemon.dynodao.processor.schema.index.DynamoIndex;
import org.lemon.dynodao.processor.schema.index.IndexType;

import javax.lang.model.element.TypeElement;
import java.util.Set;

import static org.lemon.dynodao.processor.util.StreamUtil.toLinkedHashSet;

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

    /**
     * @return the table index
     */
    public DynamoIndex getTableIndex() {
        return getIndexes().stream()
                .filter(index -> index.getIndexType().equals(IndexType.TABLE))
                .findFirst().orElseThrow(() -> new IllegalStateException("expected there to always be a table 'index'"));
    }

    /**
     * @return all local secondary indexes, if any
     */
    public Set<DynamoIndex> getLocalSecondaryIndexes() {
        return getIndexes().stream()
                .filter(index -> index.getIndexType().equals(IndexType.LOCAL_SECONDARY_INDEX))
                .collect(toLinkedHashSet());
    }

    /**
     * @return <tt>true</tt> if the schema has any local secondary indexes, <tt>false</tt> otherwise
     */
    public boolean hasLocalSecondaryIndexes() {
        return getIndexes().stream().anyMatch(index -> index.getIndexType().equals(IndexType.LOCAL_SECONDARY_INDEX));
    }

    /**
     * @return all global secondary indexes, if any
     */
    public Set<DynamoIndex> getGlobalSecondaryIndexes() {
        return getIndexes().stream()
                .filter(index -> index.getIndexType().equals(IndexType.GLOBAL_SECONDARY_INDEX))
                .collect(toLinkedHashSet());
    }

    /**
     * @return <tt>true</tt> if the schema has any global secondary indexes, <tt>false</tt> otherwise
     */
    public boolean hasGlobalSecondaryIndexes() {
        return getIndexes().stream().anyMatch(index -> index.getIndexType().equals(IndexType.GLOBAL_SECONDARY_INDEX));
    }

}
