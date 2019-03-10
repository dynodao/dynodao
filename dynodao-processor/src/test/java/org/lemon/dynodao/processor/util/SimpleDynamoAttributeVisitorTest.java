package org.lemon.dynodao.processor.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.schema.attribute.BinaryDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.BinarySetDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.BooleanDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.ListDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.MapDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.NullDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.NumberDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.NumberSetDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.StringDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.StringSetDynamoAttribute;
import org.lemon.dynodao.processor.test.AbstractUnitTest;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class SimpleDynamoAttributeVisitorTest extends AbstractUnitTest {

    private static final Object ARG = "arg";

    private static final Object DEFAULT_VALUE = "default-value";

    @Mock private DynamoAttribute dynamoAttributeMock;

    private SimpleDynamoAttributeVisitor<Object, Object> classUnderTestSpy; // spy to verify arguments explicitly

    @BeforeEach
    void setup() {
        classUnderTestSpy = spy(new SimpleDynamoAttributeVisitor<>(DEFAULT_VALUE));
    }

    @Test
    void defaultAction_defaultCtor_returnsNull() {
        Object defaultValue = new SimpleDynamoAttributeVisitor<>().defaultAction(dynamoAttributeMock, ARG);
        assertThat(defaultValue).isNull();
    }

    @Test
    void defaultAction_defaultInitialized_returnsDefaultValue() {
        Object defaultValue = classUnderTestSpy.defaultAction(dynamoAttributeMock, ARG);
        assertThat(defaultValue).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
    }

    @Test
    void visit_onlyUseCase_returnsDefaultAction() {
        Object visit = classUnderTestSpy.visit(dynamoAttributeMock, ARG);
        assertThat(visit).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(dynamoAttributeMock, ARG);
    }

    @Test
    void visitBinary_onlyUseCase_returnsDefaultAction() {
        BinaryDynamoAttribute attribute = BinaryDynamoAttribute.builder().build();
        Object visitBinary = classUnderTestSpy.visitBinary(attribute, ARG);
        assertThat(visitBinary).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(attribute, ARG);
    }

    @Test
    void visitBinarySet_onlyUseCase_returnsDefaultAction() {
        BinarySetDynamoAttribute attribute = BinarySetDynamoAttribute.builder().build();
        Object visitBinarySet = classUnderTestSpy.visitBinarySet(attribute, ARG);
        assertThat(visitBinarySet).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(attribute, ARG);
    }

    @Test
    void visitBoolean_onlyUseCase_returnsDefaultAction() {
        BooleanDynamoAttribute attribute = BooleanDynamoAttribute.builder().build();
        Object visitBoolean = classUnderTestSpy.visitBoolean(attribute, ARG);
        assertThat(visitBoolean).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(attribute, ARG);
    }

    @Test
    void visitList_onlyUseCase_returnsDefaultAction() {
        ListDynamoAttribute attribute = ListDynamoAttribute.builder().build();
        Object visitList = classUnderTestSpy.visitList(attribute, ARG);
        assertThat(visitList).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(attribute, ARG);
    }

    @Test
    void visitDocument_onlyUseCase_returnsDefaultAction() {
        DocumentDynamoAttribute attribute = DocumentDynamoAttribute.builder().build();
        Object visitDocument = classUnderTestSpy.visitDocument(attribute, ARG);
        assertThat(visitDocument).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(attribute, ARG);
    }

    @Test
    void visitMap_onlyUseCase_returnsDefaultAction() {
        MapDynamoAttribute attribute = MapDynamoAttribute.builder().build();
        Object visitMap = classUnderTestSpy.visitMap(attribute, ARG);
        assertThat(visitMap).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(attribute, ARG);
    }

    @Test
    void visitNumber_onlyUseCase_returnsDefaultAction() {
        NumberDynamoAttribute attribute = NumberDynamoAttribute.builder().build();
        Object visitNumber = classUnderTestSpy.visitNumber(attribute, ARG);
        assertThat(visitNumber).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(attribute, ARG);
    }

    @Test
    void visitNumberSet_onlyUseCase_returnsDefaultAction() {
        NumberSetDynamoAttribute attribute = NumberSetDynamoAttribute.builder().build();
        Object visitNumberSet = classUnderTestSpy.visitNumberSet(attribute, ARG);
        assertThat(visitNumberSet).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(attribute, ARG);
    }

    @Test
    void visitNull_onlyUseCase_returnsDefaultAction() {
        NullDynamoAttribute attribute = NullDynamoAttribute.builder().build();
        Object visitNull = classUnderTestSpy.visitNull(attribute, ARG);
        assertThat(visitNull).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(attribute, ARG);
    }

    @Test
    void visitString_onlyUseCase_returnsDefaultAction() {
        StringDynamoAttribute attribute = StringDynamoAttribute.builder().build();
        Object visitString = classUnderTestSpy.visitString(attribute, ARG);
        assertThat(visitString).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(attribute, ARG);
    }

    @Test
    void visitStringSet_onlyUseCase_returnsDefaultAction() {
        StringSetDynamoAttribute attribute = StringSetDynamoAttribute.builder().build();
        Object visitStringSet = classUnderTestSpy.visitStringSet(attribute, ARG);
        assertThat(visitStringSet).isEqualTo(DEFAULT_VALUE).isSameAs(DEFAULT_VALUE);
        verify(classUnderTestSpy).defaultAction(attribute, ARG);
    }

}
