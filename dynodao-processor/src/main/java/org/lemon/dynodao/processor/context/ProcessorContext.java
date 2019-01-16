package org.lemon.dynodao.processor.context;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import lombok.RequiredArgsConstructor;

/**
 * Keeps contextual data for the application (AP) scope.
 */
@RequiredArgsConstructor
public class ProcessorContext {

    private final ProcessingEnvironment processingEnvironment;

    private RoundEnvironment roundEnvironment;

    private final Set<ProcessorMessage> messages = new LinkedHashSet<>();

    /**
     * @return the {@link Elements} utils
     */
    public Elements getElementUtils() {
        return processingEnvironment.getElementUtils();
    }

    /**
     * @return the {@link Types} utils
     */
    public Types getTypeUtils() {
        return processingEnvironment.getTypeUtils();
    }

    /**
     * @return the {@link Filer}
     */
    public Filer getFiler() {
        return processingEnvironment.getFiler();
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
        Objects.requireNonNull(roundEnvironment, "ProcessContext#roundEnvironment is null; ensure newRound is called");
        return roundEnvironment;
    }

    /**
     * @param args the objects to display
     * @return #submitErrorMessage
     */
    public ProcessorMessage submitError(Object... args) {
        return submitErrorMessage(new String(new char[args.length]).replace("\0", "%s\n"), args);
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
     * @return <tt>true</tt> if any error messages have been submitted, <tt>false</tt> otherwise
     */
    public boolean hasErrors() {
        return messages.stream().anyMatch(message -> message.getKind().equals(Diagnostic.Kind.ERROR));
    }

    /**
     * Displays all of the messages accrued in this round.
     */
    public void emitMessages() {
        messages.forEach(message -> message.submit(processingEnvironment.getMessager()));
        messages.clear();
    }

}
