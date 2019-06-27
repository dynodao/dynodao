package com.github.dynodao.processor.itest.validation.unable_to_serialize;

import com.github.dynodao.processor.itest.AbstractResourceCompilingTest;
import com.google.testing.compile.Compilation;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;

class UnableToSerializeTest extends AbstractResourceCompilingTest {

    @Test
    void compile_schemaContainsTypeWhichCannotBeSerialized_errorIssuedOnField() {
        JavaFileObject schema = getSchema("UnableToSerialize");
        Compilation compilation = compile(schema);
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(1);
        assertThat(compilation)
                .hadErrorContaining("unable to parse [unknownType] into a dynamo attribute; specify either a type or field level override")
                .inFile(schema)
                .onLine(12)
                .atColumn(20);
    }

    @Test
    void compile_schemaContainsMultipleTypesWhichCannotBeSerialized_errorIssuedOnEachField() {
        JavaFileObject schema = getSchema("UnableToSerializeMultipleFields");
        Compilation compilation = compile(schema);
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(2);
        assertThat(compilation)
                .hadErrorContaining("unable to parse [unknownType1] into a dynamo attribute; specify either a type or field level override")
                .inFile(schema)
                .onLine(14)
                .atColumn(20);
        assertThat(compilation)
                .hadErrorContaining("unable to parse [unknownType2] into a dynamo attribute; specify either a type or field level override")
                .inFile(schema)
                .onLine(15)
                .atColumn(33);
    }

}
