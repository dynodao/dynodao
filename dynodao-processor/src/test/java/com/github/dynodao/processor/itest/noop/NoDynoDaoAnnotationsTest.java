package com.github.dynodao.processor.itest.noop;

import com.google.testing.compile.Compilation;
import org.assertj.core.api.Assertions;
import com.github.dynodao.processor.itest.AbstractCompilingTest;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;

class NoDynoDaoAnnotationsTest extends AbstractCompilingTest {

    @Test
    void noClassesWithDynoDaoAnnotation_onlyUseCase_processorDoesNothing() {
        JavaFileObject schema = FILE_MANAGER.getJavaFileObjects(getFileName()).iterator().next();
        Compilation compilation = compile(schema);
        assertThat(compilation).succeededWithoutWarnings();
        Assertions.assertThat(compilation.generatedFiles())
                .hasSize(1) // the schema class file is generated
                .allMatch(java -> java.getName().endsWith("Schema.class"));
    }

    private String getFileName() {
        String name = Schema.class.getCanonicalName().replace('.', '/') + ".java";
        return Schema.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "../../src/test/java/" + name;
    }

}
