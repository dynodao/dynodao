package org.lemon.dynodao.processor.itest.hash_key_only;

import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

public class HashKeyOnlyTest extends AbstractSourceCompilingTest {

    @Override
    protected Class<?> getCompilationUnitUnderTest() {
        return Schema.class;
    }

}
