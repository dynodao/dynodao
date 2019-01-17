package org.lemon.dynodao.processor.itest;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import lombok.SneakyThrows;
import org.junit.Ignore;
import org.lemon.dynodao.processor.DynoDaoProcessor;
import org.lemon.dynodao.processor.test.AbstractUnitTest;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Ignore
public abstract class AbstractIntegrationTest extends AbstractUnitTest {

    /**
     * Places the type into the default package and compiles the resultant file.
     * @param typeSpec the type to compile, will be put into the default package
     * @return the compilation result, for assertions
     */
    protected Compilation compile(TypeSpec typeSpec) {
        return compile(JavaFile.builder("", typeSpec)
                .skipJavaLangImports(true)
                .indent("    ")
                .build());
    }

    /**
     * Compiles the in memory java file, returning the compilation result.
     * @param javaFile the file to compile
     * @return the compilation result, for assertions
     */
    protected Compilation compile(JavaFile javaFile) {
        return compile(javaFile.toJavaFileObject());
    }

    /**
     * Compiles the java file object, returning the compilation result.
     * @param javaFileObject the file to compile
     * @return the compilation result, for assertions
     */
    protected final Compilation compile(JavaFileObject javaFileObject) {
        return Compiler.javac()
                .withProcessors(new DynoDaoProcessor())
                .compile(javaFileObject);
    }

    /**
     * Returns the Schema source file.
     * // TODO docs here
     * @return the Schema source file
     */
    protected final JavaFileObject getSchemaResource() {
        String packageName = getClass().getPackage().getName();
        String asPackage = toSnakeCase(getClass().getSimpleName());
        String schema = "Schema";
        String javaPath = String.format("%s.%s.%s", packageName, asPackage, schema);
        return JavaFileObjects.forResource(javaPath.replace('.', '/') + ".java");
    }

    private String toSnakeCase(String className) {
        return Arrays.stream(className.split("(?=\\p{Upper})"))
                .collect(joining("_"))
                .toLowerCase();
    }

    protected final Map<String, JavaFileObject> getAdditionalFiles() {
        String packageName = getClass().getPackage().getName();
        String asPackage = toSnakeCase(getClass().getSimpleName());
        String javaPath = String.format("%s.%s.", packageName, asPackage);
        return getResourceFiles(javaPath.replace('.', '/')).stream()
                .peek(System.out::println)
                .filter(name -> !name.endsWith("/Schema.java"))
                .collect(toMap(path -> path.replaceFirst(".java", "").replace('/', '.'), JavaFileObjects::forResource));
    }

    @SneakyThrows(IOException.class)
    private List<String> getResourceFiles(String path) {
        List<String> resourceNames = new ArrayList<>();

        try (InputStream in = getResourceAsStream(path); BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;
            while ((resource = br.readLine()) != null) {
                resourceNames.add(path + resource);
            }
        }

        return resourceNames;
    }

    private InputStream getResourceAsStream(String resource) {
        InputStream in = getContextClassLoader().getResourceAsStream(resource);
        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @SneakyThrows({ ClassNotFoundException.class, InstantiationException.class, IllegalAccessException.class })
    private Processor lombok() {
        return (Processor) Class.forName("lombok.launch.AnnotationProcessorHider$AnnotationProcessor").newInstance();
    }
}
