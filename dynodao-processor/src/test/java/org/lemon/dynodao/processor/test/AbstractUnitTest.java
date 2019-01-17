package org.lemon.dynodao.processor.test;

import static java.util.stream.Collectors.joining;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.lemon.dynodao.processor.DynoDaoProcessor;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import java.util.Arrays;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractUnitTest {

    /**
     * Initials any mockito mock annotations in this class.
     * <p>
     * Though this class uses the mockito junit runner, it may be overridden by subclasses.
     */
    @Before
    public void initMocksIfRequired() {
        RunWith runWith = getClass().getAnnotation(RunWith.class);
        if (runWith == null || !runWith.value().equals(MockitoJUnitRunner.class)) {
            initMocks(this);
        }
    }

}
