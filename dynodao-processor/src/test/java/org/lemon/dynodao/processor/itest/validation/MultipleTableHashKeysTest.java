package org.lemon.dynodao.processor.itest.validation;

import static com.google.testing.compile.CompilationSubject.assertThat;

import com.google.testing.compile.Compilation;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractIntegrationTest;

import javax.tools.JavaFileObject;

public class MultipleTableHashKeysTest extends AbstractIntegrationTest {

    @Test
    public void schemaHasMultipleHashKeysSpecified_errorsIssuedOnEachDynamoDBHashKeyAnnotation() {
        JavaFileObject schema = getSchemaResource();
        Compilation compilation = compile(schema);
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(2);
        assertThat(compilation)
                .hadErrorContaining("@DynamoDBHashKey must exist on exactly one attribute.")
                .inFile(schema)
                .onLine(9)
                .atColumn(5);
        assertThat(compilation)
                .hadErrorContaining("@DynamoDBHashKey must exist on exactly one attribute.")
                .inFile(schema)
                .onLine(12)
                .atColumn(5);
    }

}
