package org.lemon.dynodao.processor.itest.table;

import static com.google.testing.compile.CompilationSubject.assertThat;

import com.google.testing.compile.Compilation;
import org.junit.Ignore;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractIntegrationTest;

import javax.tools.JavaFileObject;

@Ignore
public class HashKeyOnlyTest extends AbstractIntegrationTest {

    @Test
    public void schemaWithSingleHashKeyOnly_correctFilesGenerated() {
        JavaFileObject schema = getSchemaResource();
        Compilation compilation = compile(schema);
        assertThat(compilation).succeeded();
        System.out.println(getAdditionalFiles().keySet());
        getAdditionalFiles().forEach((name, file) -> {
            assertThat(compilation)
                    .generatedSourceFile(name)
                    .containsElementsIn(file);
        });
    }
}
