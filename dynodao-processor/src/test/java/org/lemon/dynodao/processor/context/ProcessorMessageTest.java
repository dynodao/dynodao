package org.lemon.dynodao.processor.context;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;
import org.mockito.Mock;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.IllegalFormatException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProcessorMessageTest extends AbstractUnitTest {

    private static final Diagnostic.Kind KIND = Diagnostic.Kind.ERROR;

    @Mock private Element elementMock;
    @Mock private AnnotationMirror annotationMirrorMock;
    @Mock private AnnotationValue annotationValueMock;

    private ProcessorMessage createInstance() {
        return new ProcessorMessage(KIND, "message");
    }

    @Test
    public void ctor_noFormatArgs_messageCorrect() {
        ProcessorMessage classUnderTest = new ProcessorMessage(KIND, "message");
        assertThat(classUnderTest)
                .extracting("kind", "message")
                .containsExactly(KIND, "message");
        assertThat(classUnderTest).hasNoNullFieldsOrPropertiesExcept("element", "annotationMirror", "annotationValue");
    }

    @Test
    public void ctor_formatArgs_messageCorrect() {
        ProcessorMessage classUnderTest = new ProcessorMessage(KIND, "message is %s", "argumentative");
        assertThat(classUnderTest)
                .extracting("kind", "message")
                .containsExactly(KIND, "message is argumentative");
        assertThat(classUnderTest).hasNoNullFieldsOrPropertiesExcept("element", "annotationMirror", "annotationValue");
    }

    @Test(expected = IllegalFormatException.class)
    public void ctor_badFormat_throwsIllegalFormatException() {
        new ProcessorMessage(KIND, "message is %s %s", "argumentative, but very wrong");
    }

    @Test
    public void atElement_onlyUseCase_setsElementReturnsSelf() {
        ProcessorMessage classUnderTest = createInstance();
        ProcessorMessage ret = classUnderTest.atElement(elementMock);
        assertThat(ret).isSameAs(classUnderTest);
        assertThat(classUnderTest.getElement()).isEqualTo(elementMock);
    }

    @Test(expected = NullPointerException.class)
    public void atAnnotation_elementNotSetAnnotationMirror_throwsNullPointerException() {
        createInstance().atAnnotation(annotationMirrorMock);
    }

    @Test
    public void atAnnotation_annotationMirror_setsAnnotationReturnsSelf() {
        ProcessorMessage classUnderTest = createInstance().atElement(elementMock);
        ProcessorMessage ret = classUnderTest.atAnnotation(annotationMirrorMock);
        assertThat(ret).isSameAs(classUnderTest);
        assertThat(classUnderTest.getAnnotationMirror()).isEqualTo(annotationMirrorMock);
    }

    @Test(expected = NullPointerException.class)
    public void atAnnotation_elementNotSetClass_throwsNullPointerException() {
        createInstance().atAnnotation(Test.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void atAnnotation_annotationClassNotOnElement_throwsIllegalArgumentException() {
        ProcessorMessage classUnderTest = createInstance().atElement(elementMock);
        classUnderTest.atAnnotation(Test.class);
    }

    @Test
    public void equals_typicalUseCase_correct() {
        EqualsVerifier.forClass(ProcessorMessage.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}