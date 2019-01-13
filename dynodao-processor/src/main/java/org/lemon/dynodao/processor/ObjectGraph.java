package org.lemon.dynodao.processor;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Component(modules = ContextModule.class)
public interface ObjectGraph {

    void inject(DynoDaoProcessor processor);

}

@Module
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ContextModule {

    private final ProcessorContext processorContext;

    @Provides
    ProcessorContext providesProcessorContext() {
        return processorContext;
    }

}