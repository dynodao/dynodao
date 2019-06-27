package com.github.dynodao.processor.schema;

import com.github.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import com.github.dynodao.processor.schema.index.DynamoIndex;
import com.github.dynodao.processor.schema.index.IndexType;
import com.github.dynodao.processor.test.AbstractUnitTest;
import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DynamoSchemaTest extends AbstractUnitTest {

    @Mock private TypeElement typeElementMock;

    @Test
    void getDocumentElement_onlyUseCase_returnsDocumentElement() {
        DynamoSchema schema = schema()
                .document(DocumentDynamoAttribute.builder()
                        .element(typeElementMock)
                        .build())
                .build();
        TypeElement documentElement = schema.getDocumentElement();
        assertThat(documentElement).isEqualTo(typeElementMock);
    }

    @Test
    void getTableIndex_noIndexes_throwsIllegalStageException() {
        assertThatThrownBy(() -> schema().build().getTableIndex()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getTableIndex_noTableIndex_throwsIllegalStageException() {
        assertThatThrownBy(() -> schema()
                .indexes(singleton(index().indexType(IndexType.LOCAL_SECONDARY_INDEX).build()))
                .build().getTableIndex()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getTableIndex_tableExists_returnsTable() {
        DynamoIndex table = index().build();
        DynamoSchema schema = schema()
                .indexes(singleton(table))
                .build();
        assertThat(schema.getTableIndex()).isEqualTo(table);
    }

    @Test
    void getLocalSecondaryIndexes_none_returnsEmptySet() {
        assertThat(schema().build().getLocalSecondaryIndexes()).isEmpty();
    }

    @Test
    void getLocalSecondaryIndexes_singleIndex_returnsIndex() {
        DynamoIndex lsi = index()
                .indexType(IndexType.LOCAL_SECONDARY_INDEX)
                .build();
        DynamoSchema schema = schema()
                .indexes(singleton(lsi))
                .build();
        assertThat(schema.getLocalSecondaryIndexes()).containsExactly(lsi);
    }

    @Test
    void getLocalSecondaryIndexes_multipleIndexes_returnsIndexesInOrder() {
        DynamoIndex lsi1 = index()
                .name("lsi1")
                .indexType(IndexType.LOCAL_SECONDARY_INDEX)
                .build();
        DynamoIndex lsi2 = index()
                .name("lsi2")
                .indexType(IndexType.LOCAL_SECONDARY_INDEX)
                .build();
        DynamoSchema schema = schema()
                .indexes(setOf(lsi1, lsi2))
                .build();
        assertThat(schema.getLocalSecondaryIndexes()).containsExactly(lsi1, lsi2);
    }

    @Test
    void hasLocalSecondaryIndexes_none_returnsFalse() {
        assertThat(schema().build().hasLocalSecondaryIndexes()).isFalse();
    }

    @Test
    void hasLocalSecondaryIndexes_hasIndex_returnsTrue() {
        DynamoIndex lsi = index()
                .indexType(IndexType.LOCAL_SECONDARY_INDEX)
                .build();
        DynamoSchema schema = schema()
                .indexes(singleton(lsi))
                .build();
        assertThat(schema.hasLocalSecondaryIndexes()).isTrue();
    }

    @Test
    void getGlobalSecondaryIndexes_none_returnsEmptySet() {
        assertThat(schema().build().getGlobalSecondaryIndexes()).isEmpty();
    }

    @Test
    void getGlobalSecondaryIndexes_singleIndex_returnsIndex() {
        DynamoIndex gsi = index()
                .indexType(IndexType.GLOBAL_SECONDARY_INDEX)
                .build();
        DynamoSchema schema = schema()
                .indexes(singleton(gsi))
                .build();
        assertThat(schema.getGlobalSecondaryIndexes()).containsExactly(gsi);
    }

    @Test
    void getGlobalSecondaryIndexes_multipleIndexes_returnsIndexesInOrder() {
        DynamoIndex gsi1 = index()
                .name("gsi1")
                .indexType(IndexType.GLOBAL_SECONDARY_INDEX)
                .build();
        DynamoIndex gsi2 = index()
                .name("gsi2")
                .indexType(IndexType.GLOBAL_SECONDARY_INDEX)
                .build();
        DynamoSchema schema = schema()
                .indexes(setOf(gsi1, gsi2))
                .build();
        assertThat(schema.getGlobalSecondaryIndexes()).containsExactly(gsi1, gsi2);
    }

    @Test
    void hasGlobalSecondaryIndexes_none_returnsFalse() {
        assertThat(schema().build().hasGlobalSecondaryIndexes()).isFalse();
    }

    @Test
    void hasGlobalSecondaryIndexes_hasIndex_returnsTrue() {
        DynamoIndex lsi = index()
                .indexType(IndexType.GLOBAL_SECONDARY_INDEX)
                .build();
        DynamoSchema schema = schema()
                .indexes(singleton(lsi))
                .build();
        assertThat(schema.hasGlobalSecondaryIndexes()).isTrue();
    }

    @Test
    void toString_typicalUseCase_includesAllFields() {
        ToStringVerifier.forClass(DynamoSchema.class).verify();
    }

    @Test
    void equals_typicalUseCase_correct() {
        EqualsVerifier.forClass(DynamoSchema.class).verify();
    }

    private DynamoSchema.DynamoSchemaBuilder schema() {
        return DynamoSchema.builder()
                .tableName("tableName")
                .document(DocumentDynamoAttribute.builder().build())
                .indexes(emptySet());
    }

    private DynamoIndex.DynamoIndexBuilder index() {
        return DynamoIndex.builder()
                .name("index-name")
                .indexType(IndexType.TABLE);
    }

    private Set<DynamoIndex> setOf(DynamoIndex index1, DynamoIndex index2) {
        return new LinkedHashSet<>(Arrays.asList(index1, index2));
    }

}
