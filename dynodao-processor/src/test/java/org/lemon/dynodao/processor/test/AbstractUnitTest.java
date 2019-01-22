package org.lemon.dynodao.processor.test;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.MockitoAnnotations.initMocks;

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
