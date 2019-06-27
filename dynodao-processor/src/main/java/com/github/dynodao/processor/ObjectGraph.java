package com.github.dynodao.processor;

import com.github.dynodao.processor.context.ProcessorContext;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;

/**
 * Dagger component to inject the processor.
 */
@Singleton
@Component(modules = ContextModule.class)
interface ObjectGraph {

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

    @Provides @Singleton
    ProcessorContext providesProcessorContext() {
        return processorContext;
    }
}
