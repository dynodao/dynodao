package org.lemon.dynodao.processor.context;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides access to creating compiler messages.
 */
@Singleton
public class ProcessorMessager {

    @Inject ProcessorContext processorContext;

    private final List<ProcessorMessage> messages = new ArrayList<>();

    @Inject ProcessorMessager() { }

    /**
     * @param args the objects to display
     * @return #submitError
     */
    public ProcessorMessage _submitError(Object... args) {
        return submitError(new String(new char[args.length]).replace("\0", "%s\n"), args);
    }

    /**
     * Produces and returns a new error, which will be displayed at the end of the round.
     * The message can be modified to add additional context after return.
     * @param format the error format message
     * @param args the format arguments
     * @return the newly created message
     */
    public ProcessorMessage submitError(String format, Object... args) {
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
        messages.stream()
                .distinct()
                .forEach(message -> message.emit(processorContext.getProcessingEnvironment().getMessager()));
        messages.clear();
    }

}
