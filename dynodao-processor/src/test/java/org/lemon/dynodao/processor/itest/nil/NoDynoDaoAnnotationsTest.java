package org.lemon.dynodao.processor.itest.nil;

import com.google.testing.compile.Compilation;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractResourceCompilingTest;

import static com.google.testing.compile.CompilationSubject.assertThat;

public class NoDynoDaoAnnotationsTest extends AbstractResourceCompilingTest {

    @Test
    public void noClassesWithDynoDaoAnnotation_processorDoesNothing() {
        Compilation compilation = compile(getSchemaResource());
        assertThat(compilation).succeededWithoutWarnings();
    }

}
