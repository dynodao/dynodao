package org.lemon.dynodao.processor.node;

import com.jparams.verifier.tostring.ToStringVerifier;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.schema.DynamoSchema;
import org.lemon.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import org.lemon.dynodao.processor.schema.index.DynamoIndex;
import org.lemon.dynodao.processor.schema.index.IndexType;
import org.lemon.dynodao.processor.serialize.SerializerTypeSpec;
import org.lemon.dynodao.processor.test.AbstractUnitTest;
import org.mockito.Mock;

import javax.lang.model.element.TypeElement;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.emptySet;

class NodeClassDataTest extends AbstractUnitTest {

    @Mock private TypeElement typeElementMock;

    @Test
    void isStagedBuilder_noIndex_returnsTrue() {
        assertThat(new NodeClassData(null, null).isStagedBuilder()).isTrue();
    }

    @Test
    void isStagedBuilder_withIndex_returnsFalse() {
        NodeClassData node = new NodeClassData(null, null)
                .withIndex(index().build(), KeyLengthType.HASH);
        assertThat(node.isStagedBuilder()).isFalse();
    }

    @Test
    void getDocumentElement_onlyUseCase_returnsElementFromSchema() {
        NodeClassData node = new NodeClassData(schema()
                .document(DocumentDynamoAttribute.builder()
                        .element(typeElementMock)
                        .build())
                .build(), null);
        assertThat(node.getDocumentElement()).isEqualTo(typeElementMock);
    }

    @Test
    void toString_typicalUseCase_includesAllFields() {
        ToStringVerifier.forClass(NodeClassData.class)
                .withPrefabValue(TypeSpec.class, TypeSpec.classBuilder("class1").build())
                .verify();
    }

    @Test
    void equals_typicalUseCase_correct() {
        TypeSpec typeSpec1 = TypeSpec.classBuilder("class1").build();
        TypeSpec typeSpec2 = TypeSpec.classBuilder("class2").build();
        SerializerTypeSpec serializerTypeSpec1 = SerializerTypeSpec.builder().typeSpec(typeSpec1).build();
        SerializerTypeSpec serializerTypeSpec2 = SerializerTypeSpec.builder().typeSpec(typeSpec2).build();

        EqualsVerifier.forClass(NodeClassData.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .withPrefabValues(TypeName.class, ClassName.get(Object.class), ClassName.get(NodeClassData.class))
                .withPrefabValues(SerializerTypeSpec.class, serializerTypeSpec1, serializerTypeSpec2)
                .withPrefabValues(TypeSpec.class, typeSpec1, typeSpec2)
                .withPrefabValues(NodeClassData.class, new NodeClassData(null, serializerTypeSpec1), new NodeClassData(null, serializerTypeSpec2))
                .verify();
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
                .indexType(IndexType.TABLE)
                .hashKey(DocumentDynamoAttribute.builder().build())
                .rangeKey(Optional.of(DocumentDynamoAttribute.builder().build()));
    }

}
