package com.github.dynodao.processor.itest.validation.no_hash_key;

import com.github.dynodao.processor.itest.AbstractResourceCompilingTest;
import com.google.testing.compile.Compilation;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;

class NoTableHashKeyTest extends AbstractResourceCompilingTest {

    @Test
    void compile_schemaHasNoHashKeySpecified_errorIssuedOnDynoDaoAnnotation() {
        JavaFileObject schema = getSchema("NoTableHashKey");
        Compilation compilation = compile(schema);
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(2);
        assertThat(compilation)
                .hadErrorContaining("@DynoDaoHashKey must exist on exactly one scalar attribute, but none found.")
                .inFile(schema)
                .onLine(6)
                .atColumn(1);
    }

}
