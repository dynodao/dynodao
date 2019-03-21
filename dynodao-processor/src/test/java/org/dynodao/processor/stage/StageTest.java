package org.dynodao.processor.stage;

import com.jparams.verifier.tostring.ToStringVerifier;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.dynodao.processor.schema.DynamoSchema;
import org.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import org.dynodao.processor.schema.index.DynamoIndex;
import org.dynodao.processor.schema.index.IndexType;
import org.dynodao.processor.serialize.SerializerTypeSpec;
import org.dynodao.processor.test.AbstractUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.lang.model.element.TypeElement;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.emptySet;

class StageTest extends AbstractUnitTest {

    @Mock private TypeElement typeElementMock;

    @Test
    void isStagedBuilder_noIndex_returnsTrue() {
        assertThat(new Stage(null, null).isStagedBuilder()).isTrue();
    }

    @Test
    void isStagedBuilder_withIndex_returnsFalse() {
        Stage stage = new Stage(null, null)
                .withIndex(index().build(), KeyLengthType.HASH);
        assertThat(stage.isStagedBuilder()).isFalse();
    }

    @Test
    void getDocumentElement_onlyUseCase_returnsElementFromSchema() {
        Stage stage = new Stage(schema()
                .document(DocumentDynamoAttribute.builder()
                        .element(typeElementMock)
                        .build())
                .build(), null);
        assertThat(stage.getDocumentElement()).isEqualTo(typeElementMock);
    }

    @Test
    void toString_typicalUseCase_includesAllFields() {
        ToStringVerifier.forClass(Stage.class)
                .withPrefabValue(TypeSpec.class, TypeSpec.classBuilder("class1").build())
                .verify();
    }

    @Test
    void equals_typicalUseCase_correct() {
        TypeSpec typeSpec1 = TypeSpec.classBuilder("class1").build();
        TypeSpec typeSpec2 = TypeSpec.classBuilder("class2").build();
        SerializerTypeSpec serializerTypeSpec1 = SerializerTypeSpec.builder().typeSpec(typeSpec1).build();
        SerializerTypeSpec serializerTypeSpec2 = SerializerTypeSpec.builder().typeSpec(typeSpec2).build();

        EqualsVerifier.forClass(Stage.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .withPrefabValues(TypeName.class, ClassName.get(Object.class), ClassName.get(Stage.class))
                .withPrefabValues(SerializerTypeSpec.class, serializerTypeSpec1, serializerTypeSpec2)
                .withPrefabValues(TypeSpec.class, typeSpec1, typeSpec2)
                .withPrefabValues(Stage.class, new Stage(null, serializerTypeSpec1), new Stage(null, serializerTypeSpec2))
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
