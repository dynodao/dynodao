package org.lemon.dynodao.processor.schema.attribute;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class NullDynamoAttributeTest extends AbstractUnitTest {

    private static final Object VISITOR_RETURN = new Object();
    private static final Object VISITOR_ARG = new Object();

    @Mock
    private DynamoAttributeVisitor<Object, Object> dynamoAttributeVisitorMock;

    private NullDynamoAttribute createInstance() {
        return NullDynamoAttribute.builder().build();
    }

    @Test
    public void getAttributeType_onlyUseCase_returnsNull() {
        assertThat(createInstance().getAttributeType()).isEqualTo(DynamoAttributeType.NULL);
    }

    @Test
    public void accept_noArg_invokesVisitNullWithNull() {
        NullDynamoAttribute classUnderTest = createInstance();
        when(dynamoAttributeVisitorMock.visitNull(classUnderTest, null)).thenReturn(VISITOR_RETURN);
        Object accept = classUnderTest.accept(dynamoAttributeVisitorMock);
        assertThat(accept).isEqualTo(VISITOR_RETURN);
        verify(dynamoAttributeVisitorMock).visitNull(classUnderTest, null);
        verifyNoMoreInteractions(dynamoAttributeVisitorMock);
    }

    @Test
    public void accept_withArg_invokesVisitNull() {
        NullDynamoAttribute classUnderTest = createInstance();
        when(dynamoAttributeVisitorMock.visitNull(classUnderTest, VISITOR_ARG)).thenReturn(VISITOR_RETURN);
        Object accept = classUnderTest.accept(dynamoAttributeVisitorMock, VISITOR_ARG);
        assertThat(accept).isEqualTo(VISITOR_RETURN);
        verify(dynamoAttributeVisitorMock).visitNull(classUnderTest, VISITOR_ARG);
        verifyNoMoreInteractions(dynamoAttributeVisitorMock);
    }

    @Test
    public void toString_typicalUseCase_includesAllFields() {
        ToStringVerifier.forClass(NullDynamoAttribute.class).verify();
    }

    @Test
    public void equals_typicalUseCase_correct() {
        EqualsVerifier.forClass(NullDynamoAttribute.class).verify();
    }

}