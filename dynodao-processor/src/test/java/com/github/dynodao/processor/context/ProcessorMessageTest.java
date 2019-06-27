package com.github.dynodao.processor.context;

import com.github.dynodao.processor.test.AbstractUnitTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.IllegalFormatException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProcessorMessageTest extends AbstractUnitTest {

    private static final Diagnostic.Kind KIND = Diagnostic.Kind.ERROR;

    @Mock private Element elementMock;
    @Mock private AnnotationMirror annotationMirrorMock;
    @Mock private AnnotationValue annotationValueMock;

    private ProcessorMessage createInstance() {
        return new ProcessorMessage(KIND, "message");
    }

    @Test
    void ctor_noFormatArgs_messageCorrect() {
        ProcessorMessage classUnderTest = new ProcessorMessage(KIND, "message");
        assertThat(classUnderTest)
                .extracting("kind", "message")
                .containsExactly(KIND, "message");
        assertThat(classUnderTest).hasNoNullFieldsOrPropertiesExcept("element", "annotationMirror", "annotationValue");
    }

    @Test
    void ctor_formatArgs_messageCorrect() {
        ProcessorMessage classUnderTest = new ProcessorMessage(KIND, "message is %s", "argumentative");
        assertThat(classUnderTest)
                .extracting("kind", "message")
                .containsExactly(KIND, "message is argumentative");
        assertThat(classUnderTest).hasNoNullFieldsOrPropertiesExcept("element", "annotationMirror", "annotationValue");
    }

    @Test
    void ctor_badFormat_throwsIllegalFormatException() {
        assertThatThrownBy(() -> new ProcessorMessage(KIND, "message is %s %s", "argumentative, but very wrong"))
                .isInstanceOf(IllegalFormatException.class);
    }

    @Test
    void atElement_onlyUseCase_setsElementReturnsSelf() {
        ProcessorMessage classUnderTest = createInstance();
        ProcessorMessage ret = classUnderTest.atElement(elementMock);
        assertThat(ret).isSameAs(classUnderTest);
        assertThat(classUnderTest.getElement()).isEqualTo(elementMock);
    }

    @Test
    void atAnnotation_elementNotSetAnnotationMirror_throwsNullPointerException() {
        assertThatThrownBy(() -> createInstance().atAnnotation(annotationMirrorMock))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void atAnnotation_annotationMirror_setsAnnotationReturnsSelf() {
        ProcessorMessage classUnderTest = createInstance().atElement(elementMock);
        ProcessorMessage ret = classUnderTest.atAnnotation(annotationMirrorMock);
        assertThat(ret).isSameAs(classUnderTest);
        assertThat(classUnderTest.getAnnotationMirror()).isEqualTo(annotationMirrorMock);
    }

    @Test
    void atAnnotation_elementNotSetClass_throwsNullPointerException() {
        assertThatThrownBy(() -> createInstance().atAnnotation(Test.class))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void atAnnotation_annotationClassNotOnElement_throwsIllegalArgumentException() {
        ProcessorMessage classUnderTest = createInstance().atElement(elementMock);
        assertThatThrownBy(() -> classUnderTest.atAnnotation(Test.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equals_typicalUseCase_correct() {
        EqualsVerifier.forClass(ProcessorMessage.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}
