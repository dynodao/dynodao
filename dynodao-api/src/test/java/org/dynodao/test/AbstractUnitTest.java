package org.dynodao.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.mockito.MockitoAnnotations;

/**
 * Base class for any and all unit tests.
 * <p>
 * Initializes mockito annotations.
 */
@Disabled
public abstract class AbstractUnitTest {

    /**
     * Initials any mockito mock annotations in this class.
     */
    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

}
