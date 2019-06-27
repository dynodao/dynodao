package com.github.dynodao.processor.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import java.util.Objects;

/**
 * Keeps contextual data for the application (AP) scope.
 */
@RequiredArgsConstructor
public class ProcessorContext {

    @Getter private final ProcessingEnvironment processingEnvironment;

    private RoundEnvironment roundEnvironment;

    /**
     * Registers a new round into this context.
     * @param roundEnvironment the round environment
     */
    public void newRound(RoundEnvironment roundEnvironment) {
        this.roundEnvironment = roundEnvironment;
    }

    /**
     * @return the current count environment
     */
    public RoundEnvironment getRoundEnvironment() {
        Objects.requireNonNull(roundEnvironment, "ProcessContext#roundEnvironment is null; ensure newRound is called");
        return roundEnvironment;
    }

}
