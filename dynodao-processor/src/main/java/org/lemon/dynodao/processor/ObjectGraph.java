package org.lemon.dynodao.processor;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.lemon.dynodao.processor.context.ProcessorContext;

/**
 * Dagger component to inject the processor.
 */
@Component(modules = ContextModule.class)
public interface ObjectGraph {

    void inject(DynoDaoProcessor processor);

}

/**
 * Utility module to share the {@link ProcessorContext} everywhere via injection, rather than keeping it
 * as an argument to all methods.
 */
@Module
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ContextModule {

    private final ProcessorContext processorContext;

    @Provides
    ProcessorContext providesProcessorContext() {
        return processorContext;
    }
}
