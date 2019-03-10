package org.lemon.dynodao.processor.itest;

import com.google.testing.compile.Compilation;
import com.jparams.verifier.tostring.ToStringVerifier;
import lombok.SneakyThrows;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierApi;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.lemon.dynodao.processor.test.PackageScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Base class for a successful compilation test, ie the compilation unit under test is already compiled, and sources
 * are already generated an in the classpath.
 * <p>
 * This class re-compiles the compilation unit under test to account for the code coverage.
 */
@Disabled
public abstract class AbstractSourceCompilingTest extends AbstractCompilingTest {

    // only recompile once, since it takes quite a while
    private static final Map<Class<?>, Compilation> COMPILATION_RESULTS = new HashMap<>();

    /**
     * Returns the compilation unit that was compiled for the purposes of this test.
     * This class is re-compiled by this test to account for the code coverage of the package.
     * By default, this returns the class with the name {@code Schema} in the same package as the test class.
     * @return the schema "class under test"
     */
    @SneakyThrows(ClassNotFoundException.class)
    public Class<?> getCompilationUnitUnderTest() {
        String testClassName = getClass().getCanonicalName();
        String schemaClassName = testClassName.substring(0, testClassName.lastIndexOf('.') + 1) + "Schema";
        return Class.forName(schemaClassName);
    }

    /**
     * Recompiles the {@code compilationUnitUnderTest} and asserts the compilation succeeds (which, it must have),
     * and that the compilation generated the source files (which... it must have).
     * This allows for us to actually get code coverage metrics during integration tests.
     */
    @TestFactory
    Stream<DynamicTest> recompileSchemaClass_onlyUseCase_generatesFilesAndCountTowardCoverage() {
        Class<?> compilationUnit = getCompilationUnitUnderTest();
        COMPILATION_RESULTS.computeIfAbsent(compilationUnit, clazz -> compile(FILE_MANAGER.getJavaFileObjects(getFileName(clazz)).iterator().next()));
        return PackageScanner.findClassesFor(this)
                .map(clazz -> dynamicTest("recompileSchema_" + clazz.getSimpleName() + "_wasGenerated", () ->
                        assertThat(COMPILATION_RESULTS.get(getCompilationUnitUnderTest())).generatedSourceFile(clazz.getCanonicalName())));
    }

    private String getFileName(Class<?> clazz) {
        String name = clazz.getCanonicalName().replace('.', '/') + ".java";
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath() + "../../src/test/java/" + name;
    }

    /**
     * Asserts all generated sources have a valid {@code toString} implementation.
     */
    @TestFactory
    Stream<DynamicTest> toString_allGeneratedSources_validToString() {
        return PackageScanner.findClassesFor(this)
                .map(clazz -> dynamicTest(clazz.getCanonicalName() + "#toString", () -> ToStringVerifier.forClass(clazz).verify()));
    }

    /**
     * Asserts all generated sources have a valid {@code equals} and {@code hashCode} implementation.
     * For the serializer class, we check it is inherited from {@link Object}.
     */
    @TestFactory
    Stream<DynamicTest> equalsAndHashCode_allGeneratedSources_validEquals() {
        return PackageScanner.findClassesFor(this)
                .map(clazz -> dynamicTest(clazz.getCanonicalName() + "#equals", () -> {
                    EqualsVerifierApi<?> verifier = EqualsVerifier.forClass(clazz);
                    if (clazz.getName().endsWith("Serializer")) {
                        verifier.suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT);
                    }
                    verifier.verify();
                }));
    }

}
