package org.lemon.dynodao.processor.context;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import lombok.RequiredArgsConstructor;

import static com.google.common.base.Preconditions.checkState;

/**
 * Keeps contextual data for the application (AP) scope.
 */
@RequiredArgsConstructor
public class ProcessorContext {

    private final ProcessingEnvironment processingEnvironment;

    private RoundEnvironment roundEnvironment;

    private final List<ProcessorMessage> messages = new ArrayList<>();

    /**
     * @return the elements utils
     */
    public Elements getElementUtils() {
        return processingEnvironment.getElementUtils();
    }

    /**
     * @return the types utils
     */
    public Types getTypeUtils() {
        return processingEnvironment.getTypeUtils();
    }

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
        checkState(roundEnvironment != null, "ProcessContext#roundEnvironment is null; ensure newRound is called");
        return roundEnvironment;
    }

    /**
     * Produces and returns a new error, which will be displayed at the end of the round.
     * The message can be modified to add additional context after return.
     * @param format the error format message
     * @param args the format arguments
     * @return the message
     */
    public ProcessorMessage submitErrorMessage(String format, Object... args) {
        return submit(Diagnostic.Kind.ERROR, format, args);
    }

    private ProcessorMessage submit(Diagnostic.Kind kind, String format, Object... args) {
        ProcessorMessage message = new ProcessorMessage(kind, format, args);
        messages.add(message);
        return message;
    }

    /**
     * Displays all of the messages.
     */
    public void processMessages() {
        messages.forEach(message -> message.submit(processingEnvironment.getMessager()));
        messages.clear();
    }

}
