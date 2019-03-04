package org.lemon.dynodao.processor.itest.validation.no_hash_key;

import com.google.testing.compile.Compilation;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractResourceCompilingTest;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;

public class NoTableHashKeyTest extends AbstractResourceCompilingTest {

    @Test
    public void compile_schemaHasNoHashKeySpecified_errorIssuedOnDynoDaoAnnotation() {
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
