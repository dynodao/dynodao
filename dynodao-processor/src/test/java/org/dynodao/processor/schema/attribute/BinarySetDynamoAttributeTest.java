package org.dynodao.processor.schema.attribute;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.dynodao.processor.test.AbstractUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BinarySetDynamoAttributeTest extends AbstractUnitTest {

    private static final Object VISITOR_RETURN = new Object();
    private static final Object VISITOR_ARG = new Object();

    @Mock
    private DynamoAttributeVisitor<Object, Object> dynamoAttributeVisitorMock;

    private BinarySetDynamoAttribute createInstance() {
        return BinarySetDynamoAttribute.builder().build();
    }

    @Test
    void getAttributeType_onlyUseCase_returnsBinarySet() {
        assertThat(createInstance().getAttributeType()).isEqualTo(DynamoAttributeType.BINARY_SET);
    }

    @Test
    void accept_noArg_invokesVisitBinarySetWithNull() {
        BinarySetDynamoAttribute classUnderTest = createInstance();
        when(dynamoAttributeVisitorMock.visitBinarySet(classUnderTest, null)).thenReturn(VISITOR_RETURN);
        Object accept = classUnderTest.accept(dynamoAttributeVisitorMock);
        assertThat(accept).isEqualTo(VISITOR_RETURN);
        verify(dynamoAttributeVisitorMock).visitBinarySet(classUnderTest, null);
        verifyNoMoreInteractions(dynamoAttributeVisitorMock);
    }

    @Test
    void accept_withArg_invokesVisitBinarySet() {
        BinarySetDynamoAttribute classUnderTest = createInstance();
        when(dynamoAttributeVisitorMock.visitBinarySet(classUnderTest, VISITOR_ARG)).thenReturn(VISITOR_RETURN);
        Object accept = classUnderTest.accept(dynamoAttributeVisitorMock, VISITOR_ARG);
        assertThat(accept).isEqualTo(VISITOR_RETURN);
        verify(dynamoAttributeVisitorMock).visitBinarySet(classUnderTest, VISITOR_ARG);
        verifyNoMoreInteractions(dynamoAttributeVisitorMock);
    }

    @Test
    void toString_typicalUseCase_includesAllFields() {
        ToStringVerifier.forClass(BinarySetDynamoAttribute.class).verify();
    }

    @Test
    void equals_typicalUseCase_correct() {
        EqualsVerifier.forClass(BinarySetDynamoAttribute.class).verify();
    }

}
