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

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * Base class for a successful compilation test, ie the compilation unit under test is already compiled, and sources
 * are already generated an in the classpath.
 * <p>
 * This class re-compiles the compilation unit under test to account for the code coverage.
 */
@Ignore
public abstract class AbstractSourceCompilingTest extends AbstractCompilingTest {

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

    /**
     * Returns the set of classes that should not be tested for equals. This returns the {@code compilationUnitUnderTest}
     * by default. The returned set is mutable.
     * @return the set of classes to ignore equality checks for
     */
    protected Set<Class<?>> ignoreTestEqualsClasses() {
        return new HashSet<>(singletonList(getCompilationUnitUnderTest()));
    }

    /**
     * Recompiles the {@code compilationUnitUnderTest} and asserts the compilation succeeds (which, it must have).
     * This allows for us to actually get code coverage metrics during integration tests.
     */
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

    /**
     * Asserts all generated sources have a valid {@code toString} implementation.
     */
    @Test
    public void toString_allGeneratedSources_validToString() {
        ToStringVerifier.forPackage(getCompilationUnitUnderTest().getPackage().getName(), false,
                clazz -> !clazz.isAnonymousClass()).verify();
    }

    /**
     * Asserts all generated sources have a valid {@code equals} and {@code hashCode} implementation.
     * For the serializer class, we check it is inherited from {@link Object}.
     */
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
 * Scans package and identifies classes which match some criteria.
 */
@UtilityClass
class PackageScanner {

    /**
     * Scan for all classes in the same package as the test class, excluding tests, anonymous classes
     * and those classes which the test class says to ignore.
     * @param testClass the test class
     * @return classes matching criteria
     */
    static List<Class<?>> findClasses(AbstractSourceCompilingTest testClass) {
        return findClasses(testClass.getClass().getPackage().getName())
                .filter(clazz -> !testClass.ignoreTestEqualsClasses().contains(clazz))
                .filter(clazz -> !AbstractSourceCompilingTest.class.isAssignableFrom(clazz))
                .filter(clazz -> !clazz.isAnonymousClass())
                .collect(toList());
    }

    private static Stream<Class<?>> findClasses(String packageName) {
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = getResources(path);

        List<File> rootDirectories = new ArrayList<>();
        while (resources.hasMoreElements()) {
            rootDirectories.add(new File(resources.nextElement().getFile()));
        }

        return rootDirectories.stream()
                .map(rootDirectory -> findClasses(rootDirectory, packageName))
                .flatMap(List::stream);
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
