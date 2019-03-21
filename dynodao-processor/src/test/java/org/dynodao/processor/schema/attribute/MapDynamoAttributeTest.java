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

class MapDynamoAttributeTest extends AbstractUnitTest {

    private static final Object VISITOR_RETURN = new Object();
    private static final Object VISITOR_ARG = new Object();

    @Mock
    private DynamoAttributeVisitor<Object, Object> dynamoAttributeVisitorMock;

    private MapDynamoAttribute createInstance() {
        return MapDynamoAttribute.builder().build();
    }

    @Test
    void getAttributeType_onlyUseCase_returnsMap() {
        assertThat(createInstance().getAttributeType()).isEqualTo(DynamoAttributeType.MAP);
    }

    @Test
    void accept_noArg_invokesVisitMapWithNull() {
        MapDynamoAttribute classUnderTest = createInstance();
        when(dynamoAttributeVisitorMock.visitMap(classUnderTest, null)).thenReturn(VISITOR_RETURN);
        Object accept = classUnderTest.accept(dynamoAttributeVisitorMock);
        assertThat(accept).isEqualTo(VISITOR_RETURN);
        verify(dynamoAttributeVisitorMock).visitMap(classUnderTest, null);
        verifyNoMoreInteractions(dynamoAttributeVisitorMock);
    }

    @Test
    void accept_withArg_invokesVisitMap() {
        MapDynamoAttribute classUnderTest = createInstance();
        when(dynamoAttributeVisitorMock.visitMap(classUnderTest, VISITOR_ARG)).thenReturn(VISITOR_RETURN);
        Object accept = classUnderTest.accept(dynamoAttributeVisitorMock, VISITOR_ARG);
        assertThat(accept).isEqualTo(VISITOR_RETURN);
        verify(dynamoAttributeVisitorMock).visitMap(classUnderTest, VISITOR_ARG);
        verifyNoMoreInteractions(dynamoAttributeVisitorMock);
    }

    @Test
    void toString_typicalUseCase_includesAllFields() {
        ToStringVerifier.forClass(MapDynamoAttribute.class).verify();
    }

    @Test
    void equals_typicalUseCase_correct() {
        EqualsVerifier.forClass(MapDynamoAttribute.class).verify();
    }

}
