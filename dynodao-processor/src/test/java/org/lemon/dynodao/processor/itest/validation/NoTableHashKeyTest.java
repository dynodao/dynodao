package org.lemon.dynodao.processor.itest.validation;

import static com.google.testing.compile.CompilationSubject.assertThat;

import com.google.testing.compile.Compilation;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractIntegrationTest;

import javax.tools.JavaFileObject;

public class NoTableHashKeyTest extends AbstractIntegrationTest {

    @Test
    public void schemaHasNoHashKeySpecified_errorIssuedOnDynoDaoAnnotation() {
        JavaFileObject schema = getSchemaResource();
        Compilation compilation = compile(schema);
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(2);
        assertThat(compilation)
                .hadErrorContaining("@DynamoDBHashKey must exist on exactly one scalar attribute, but none found.")
                .inFile(schema)
                .onLine(6)
                .atColumn(1);
    }

}
