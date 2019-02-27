package org.lemon.dynodao.processor.context;

import org.junit.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;
import org.mockito.Mock;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProcessorContextTest extends AbstractUnitTest {

    @Mock private ProcessingEnvironment processingEnvironmentMock;
    @Mock private RoundEnvironment roundEnvironmentMock;

    @Test
    public void getRoundEnvironment_notSet_throwsNullPointerException() {
        ProcessorContext context = new ProcessorContext(processingEnvironmentMock);
        assertThatThrownBy(() -> context.getRoundEnvironment()).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void getRoundEnvironment_roundSetViaNewRound_returnsRound() {
        ProcessorContext context = new ProcessorContext(processingEnvironmentMock);
        context.newRound(roundEnvironmentMock);
        RoundEnvironment roundEnvironment = context.getRoundEnvironment();
        assertThat(roundEnvironment).isEqualTo(roundEnvironmentMock);
    }

    @Test
    public void getProcessingEnvironment_onlyUseCase_returnsProcessingEnvironment() {
        ProcessorContext context = new ProcessorContext(processingEnvironmentMock);
        ProcessingEnvironment processingEnvironment = context.getProcessingEnvironment();
        assertThat(processingEnvironment).isEqualTo(processingEnvironmentMock);
    }

}