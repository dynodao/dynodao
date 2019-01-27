package org.lemon.dynodao.processor.itest;

import com.google.testing.compile.JavaFileObjects;
import lombok.SneakyThrows;
import org.junit.Ignore;

import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@Ignore
public abstract class AbstractResourceCompilingTest extends AbstractCompilingTest {

    /**
     * Returns the Schema source file.
     * <p>
     * The schema source file is stored in the test resources.
     * TODO re-visit this, we should probably specify the resource path instead
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

    /**
     * FIXME should this exist?
     * @return
     */
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

}
