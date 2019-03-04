package org.lemon.dynodao.processor.itest.noop;

import com.google.testing.compile.Compilation;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractCompilingTest;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import static com.google.testing.compile.CompilationSubject.assertThat;

class NoDynoDaoAnnotationsTest extends AbstractCompilingTest {

    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();
    private static final StandardJavaFileManager FILE_MANAGER = COMPILER.getStandardFileManager(new DiagnosticCollector<>(), null, null);

    @Test
    void noClassesWithDynoDaoAnnotation_onlyUseCase_processorDoesNothing() {
        JavaFileObject schema = FILE_MANAGER.getJavaFileObjects(getFileName()).iterator().next();
        Compilation compilation = compile(schema);
        assertThat(compilation).succeededWithoutWarnings();
    }

    private String getFileName() {
        String name = Schema.class.getCanonicalName().replace('.', '/') + ".java";
        return Schema.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "../../src/test/java/" + name;
    }

}
