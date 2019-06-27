package com.github.dynodao.processor.itest.validation.multiple_hash_keys;

import com.github.dynodao.processor.itest.AbstractResourceCompilingTest;
import com.google.testing.compile.Compilation;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;

class MultipleTableHashKeysTest extends AbstractResourceCompilingTest {

    @Test
    void compile_schemaHasMultipleHashKeysSpecified_errorsIssuedOnEachDynoDaoHashKeyAnnotation() {
        JavaFileObject schema = getSchema("MultipleTableHashKeys");
        Compilation compilation = compile(schema);
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(2);
        assertThat(compilation)
                .hadErrorContaining("@DynoDaoHashKey must exist on exactly one attribute.")
                .inFile(schema)
                .onLine(9)
                .atColumn(5);
        assertThat(compilation)
                .hadErrorContaining("@DynoDaoHashKey must exist on exactly one attribute.")
                .inFile(schema)
                .onLine(12)
                .atColumn(5);
    }

}
