package org.lemon.dynodao.processor.itest.nil;

import static com.google.testing.compile.CompilationSubject.assertThat;

import com.google.testing.compile.Compilation;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractIntegrationTest;

public class NoDynoDaoAnnotationsTest extends AbstractIntegrationTest {

    @Test
    public void noClassesWithDynoDaoAnnotation_processorDoesNothing() {
        Compilation compilation = compile(getSchemaResource());
        assertThat(compilation).succeededWithoutWarnings();
    }

}
