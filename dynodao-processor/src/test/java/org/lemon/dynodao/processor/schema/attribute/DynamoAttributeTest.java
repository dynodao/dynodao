package org.lemon.dynodao.processor.schema.attribute;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.schema.serialize.MappingMethod;
import org.lemon.dynodao.processor.test.AbstractUnitTest;
import org.mockito.Mock;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamoAttributeTest extends AbstractUnitTest {

    @Data
    private static class DynamoAttributeStub implements DynamoAttribute {
        private final String path = null;
        private final Element element = null;
        private final TypeMirror typeMirror = null;
        private final DynamoAttributeType attributeType = null;
        private final MappingMethod serializationMethod = null;
        private final MappingMethod deserializationMethod = null;

        @Override
        public <R, P> R accept(DynamoAttributeVisitor<R, P> visitor, P arg) {
            return visitor.visit(this, arg);
        }
    }

    @Mock private DynamoAttributeVisitor<Object, Object> visitorMock;

    @Test
    void accept_onlyUseCase_callsOverloadWithNullArgument() {
        DynamoAttribute classUnderTestSpy = spy(new DynamoAttributeStub()); // spy to verify arguments explicitly
        Object expectedAccept = new Object();
        when(visitorMock.visit(classUnderTestSpy, null)).thenReturn(expectedAccept);
        Object accept = classUnderTestSpy.accept(visitorMock);
        assertThat(accept).isEqualTo(expectedAccept);
        verify(classUnderTestSpy).accept(visitorMock, null);
    }

    @Test
    void getNestedAttributesRecursively_typicalUseCase_returnsAllNestedAttributes() {
        DocumentDynamoAttribute documentDynamoAttribute = documentDynamoAttribute();

        List<DynamoAttribute> expectedNestedAttributes = new ArrayList<>();
        expectedNestedAttributes.add(binaryDynamoAttribute());

        expectedNestedAttributes.add(binarySetDynamoAttribute().getSetElement());
        expectedNestedAttributes.add(binarySetDynamoAttribute());

        expectedNestedAttributes.add(booleanDynamoAttribute());

        expectedNestedAttributes.add(listDynamoAttribute().getListElement());
        expectedNestedAttributes.add(listDynamoAttribute());

        expectedNestedAttributes.add(mapDynamoAttribute().getMapElement());
        expectedNestedAttributes.add(mapDynamoAttribute());

        expectedNestedAttributes.add(nullDynamoAttribute());

        expectedNestedAttributes.add(numberDynamoAttribute());

        expectedNestedAttributes.add(numberSetDynamoAttribute().getSetElement());
        expectedNestedAttributes.add(numberSetDynamoAttribute());

        expectedNestedAttributes.add(stringDynamoAttribute());

        expectedNestedAttributes.add(stringSetDynamoAttribute().getSetElement());
        expectedNestedAttributes.add(stringSetDynamoAttribute());

        expectedNestedAttributes.add(emptyDocumentDynamoAttribute());

        expectedNestedAttributes.add(documentDynamoAttribute);

        List<DynamoAttribute> nestedAttributes = documentDynamoAttribute.getNestedAttributesRecursively();
        assertThat(nestedAttributes)
                .containsExactly(expectedNestedAttributes.toArray(new DynamoAttribute[0]))
                .isEqualTo(expectedNestedAttributes);
    }

    private BinaryDynamoAttribute binaryDynamoAttribute() {
        return BinaryDynamoAttribute.builder().build();
    }

    private BinarySetDynamoAttribute binarySetDynamoAttribute() {
        return BinarySetDynamoAttribute.builder()
                .setElement(binaryDynamoAttribute())
                .build();
    }

    private BooleanDynamoAttribute booleanDynamoAttribute() {
        return BooleanDynamoAttribute.builder().build();
    }

    private DocumentDynamoAttribute documentDynamoAttribute() {
        return DocumentDynamoAttribute.builder()
                .attributes(Arrays.asList(
                        binaryDynamoAttribute(),
                        binarySetDynamoAttribute(),
                        booleanDynamoAttribute(),
                        listDynamoAttribute(),
                        mapDynamoAttribute(),
                        nullDynamoAttribute(),
                        numberDynamoAttribute(),
                        numberSetDynamoAttribute(),
                        stringDynamoAttribute(),
                        stringSetDynamoAttribute(),
                        emptyDocumentDynamoAttribute()
                ))
                .build();
    }

    private DocumentDynamoAttribute emptyDocumentDynamoAttribute() {
        return DocumentDynamoAttribute.builder()
                .attributes(emptyList())
                .build();
    }

    private ListDynamoAttribute listDynamoAttribute() {
        return ListDynamoAttribute.builder()
                .listElement(stringDynamoAttribute())
                .build();
    }

    private MapDynamoAttribute mapDynamoAttribute() {
        return MapDynamoAttribute.builder()
                .mapElement(stringDynamoAttribute())
                .build();
    }

    private NullDynamoAttribute nullDynamoAttribute() {
        return NullDynamoAttribute.builder().build();
    }

    private NumberDynamoAttribute numberDynamoAttribute() {
        return NumberDynamoAttribute.builder().build();
    }

    private NumberSetDynamoAttribute numberSetDynamoAttribute() {
        return NumberSetDynamoAttribute.builder()
                .setElement(numberDynamoAttribute())
                .build();
    }

    private StringDynamoAttribute stringDynamoAttribute() {
        return StringDynamoAttribute.builder().build();
    }

    private StringSetDynamoAttribute stringSetDynamoAttribute() {
        return StringSetDynamoAttribute.builder()
                .setElement(stringDynamoAttribute())
                .build();
    }

}
