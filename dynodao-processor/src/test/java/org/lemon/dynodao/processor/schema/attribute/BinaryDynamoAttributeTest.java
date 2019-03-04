package org.lemon.dynodao.processor.schema.attribute;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BinaryDynamoAttributeTest extends AbstractUnitTest {

    private static final Object VISITOR_RETURN = new Object();
    private static final Object VISITOR_ARG = new Object();

    @Mock private DynamoAttributeVisitor<Object, Object> dynamoAttributeVisitorMock;

    private BinaryDynamoAttribute createInstance() {
        return BinaryDynamoAttribute.builder().build();
    }

    @Test
    void getAttributeType_onlyUseCase_returnsBinary() {
        assertThat(createInstance().getAttributeType()).isEqualTo(DynamoAttributeType.BINARY);
    }

    @Test
    void accept_noArg_invokesVisitBinaryWithNull() {
        BinaryDynamoAttribute classUnderTest = createInstance();
        when(dynamoAttributeVisitorMock.visitBinary(classUnderTest, null)).thenReturn(VISITOR_RETURN);
        Object accept = classUnderTest.accept(dynamoAttributeVisitorMock);
        assertThat(accept).isEqualTo(VISITOR_RETURN);
        verify(dynamoAttributeVisitorMock).visitBinary(classUnderTest, null);
        verifyNoMoreInteractions(dynamoAttributeVisitorMock);
    }

    @Test
    void accept_withArg_invokesVisitBinary() {
        BinaryDynamoAttribute classUnderTest = createInstance();
        when(dynamoAttributeVisitorMock.visitBinary(classUnderTest, VISITOR_ARG)).thenReturn(VISITOR_RETURN);
        Object accept = classUnderTest.accept(dynamoAttributeVisitorMock, VISITOR_ARG);
        assertThat(accept).isEqualTo(VISITOR_RETURN);
        verify(dynamoAttributeVisitorMock).visitBinary(classUnderTest, VISITOR_ARG);
        verifyNoMoreInteractions(dynamoAttributeVisitorMock);
    }

    @Test
    void toString_typicalUseCase_includesAllFields() {
        ToStringVerifier.forClass(BinaryDynamoAttribute.class).verify();
    }

    @Test
    void equals_typicalUseCase_correct() {
        EqualsVerifier.forClass(BinaryDynamoAttribute.class).verify();
    }

}
