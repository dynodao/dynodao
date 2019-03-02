package org.lemon.dynodao.processor.itest;

import com.google.testing.compile.Compilation;
import com.jparams.verifier.tostring.ToStringVerifier;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierApi;
import nl.jqno.equalsverifier.Warning;
import org.junit.Ignore;
import org.junit.Test;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Base class for a successful compilation test, ie the compilation unit under test is already compiled, and sources
 * are already generated an in the classpath.
 * <p>
 * This class re-compiles the compilation unit under test to account for the code coverage.
 */
@Ignore
public abstract class AbstractSourceCompilingTest extends AbstractCompilingTest {

    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();
    private static final StandardJavaFileManager FILE_MANAGER = COMPILER.getStandardFileManager(new DiagnosticCollector<>(), null, null);

    // only run the recompile test once, since it takes quite a while
    private static final Set<Class<?>> COMPILE_ONCE = new HashSet<>();

    /**
     * Returns the compilation unit that was compiled for the purposes of this test.
     * This class is re-compiled by this test to account for the code coverage of the package.
     * By default, this returns the class with the name {@code Schema} in the same package as the test class.
     * @return the schema "class under test"
     */
    @SneakyThrows(ClassNotFoundException.class)
    protected Class<?> getCompilationUnitUnderTest() {
        String testClassName = getClass().getCanonicalName();
        String schemaClassName = testClassName.substring(0, testClassName.lastIndexOf('.') + 1) + "Schema";
        return Class.forName(schemaClassName);
    }

    @Test
    public void recompileSchemaClass_onlyUseCase_countTowardCodeCoverage() {
        if (COMPILE_ONCE.add(getCompilationUnitUnderTest())) {
            JavaFileObject schema = FILE_MANAGER.getJavaFileObjects(getFileName(getCompilationUnitUnderTest())).iterator().next();
            Compilation compilation = compile(schema);
            assertThat(compilation).succeeded();
        }
    }

    private String getFileName(Class<?> clazz) {
        String name = clazz.getCanonicalName().replace('.', '/') + ".java";
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath() + "../../src/test/java/" + name;
    }

    @Test
    public void toString_allGeneratedSources_validToString() {
        ToStringVerifier.forPackage(getCompilationUnitUnderTest().getPackage().getName(), false,
                clazz -> !clazz.isAnonymousClass()).verify();
    }

    @Test
    public void equalsAndHashCode_allGeneratedSources_validEquals() {
        for (Class<?> clazz : PackageScanner.findClasses(this)) {
            EqualsVerifierApi<?> verifier = EqualsVerifier.forClass(clazz);
            if (clazz.getName().endsWith("Serializer")) {
                verifier.suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT);
            }
            verifier.verify();
        }
    }

}

/**
 * Scans package and identifies classes
 */
@UtilityClass
class PackageScanner {

    /**
     * Scan for all classes in the same package as the test class
     * @param testClass package to scan
     * @return classes
     */
    static List<Class<?>> findClasses(AbstractSourceCompilingTest testClass) {
        return findClasses(testClass.getClass().getPackage().getName()).stream()
                .filter(clazz -> !clazz.equals(testClass.getCompilationUnitUnderTest()))
                .filter(clazz -> !AbstractSourceCompilingTest.class.isAssignableFrom(clazz))
                .filter(clazz -> !clazz.isAnonymousClass())
                .collect(toList());
    }

    /**
     * Scan for all classes in the given package
     * @param packageName package to scan
     * @return classes
     */
    private static List<Class<?>> findClasses(String packageName) {
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = getResources(path);

        List<File> rootDirectories = new ArrayList<>();
        while (resources.hasMoreElements()) {
            rootDirectories.add(new File(resources.nextElement().getFile()));
        }

        return rootDirectories.stream()
                .map(rootDirectory -> findClasses(rootDirectory, packageName))
                .flatMap(List::stream)
                .collect(toList());
    }

    @SneakyThrows(IOException.class)
    private static Enumeration<URL> getResources(String path) {
        return Thread.currentThread().getContextClassLoader().getResources(path);
    }

    @SneakyThrows(ReflectiveOperationException.class)
    private static List<Class<?>> findClasses(File rootDirectory, String packageName) {
        File[] files;

        if (!rootDirectory.exists() || (files = rootDirectory.listFiles()) == null) {
            return emptyList();
        }

        List<Class<?>> classes = new ArrayList<>();
        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        }
        return classes;
    }
}
