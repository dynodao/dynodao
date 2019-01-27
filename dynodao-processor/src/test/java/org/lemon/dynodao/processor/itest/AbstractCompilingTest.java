package org.lemon.dynodao.processor.itest;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import lombok.SneakyThrows;
import org.junit.Ignore;
import org.lemon.dynodao.processor.DynoDaoProcessor;
import org.lemon.dynodao.processor.test.AbstractUnitTest;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;

/**
 * Base unit test which provides access to compiler tools.
 */
@Ignore
public abstract class AbstractCompilingTest extends AbstractUnitTest {

    /**
     * Places the type into the default package and compiles the resultant file.
     * @param typeSpec the type to compile, will be put into the default package
     * @return the compilation result, for assertions
     */
    protected final Compilation compile(TypeSpec typeSpec) {
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
    protected final Compilation compile(JavaFile javaFile) {
        return compile(javaFile.toJavaFileObject());
    }

    /**
     * Compiles the java file object, returning the compilation result.
     * @param javaFileObject the file to compile
     * @return the compilation result, for assertions
     */
    protected final Compilation compile(JavaFileObject javaFileObject) {
        return Compiler.javac()
                .withProcessors(new DynoDaoProcessor(), lombok())
                .compile(javaFileObject);
    }

    @SneakyThrows({ ClassNotFoundException.class, InstantiationException.class, IllegalAccessException.class })
    private Processor lombok() {
        return (Processor) Class.forName("lombok.launch.AnnotationProcessorHider$AnnotationProcessor").newInstance();
    }

}
