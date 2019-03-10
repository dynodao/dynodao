package org.lemon.dynodao.processor.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.mockito.MockitoAnnotations;

/**
 * Base class for any and all unit tests.
 * <p>
 * Initializes mockito annotations.
 */
@Disabled
@PackageScanner.Ignore
public abstract class AbstractUnitTest {

    /**
     * Initials any mockito mock annotations in this class.
     */
    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

}
