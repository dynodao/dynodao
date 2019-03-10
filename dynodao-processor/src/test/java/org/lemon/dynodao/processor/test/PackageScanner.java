package org.lemon.dynodao.processor.test;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

/**
 * Scans package for classes which the processor may have generated.
 */
@UtilityClass
public class PackageScanner {

    /**
     * Marks this class and all subclasses of it as ignored while scanning packages for dynodao generated classes.
     */
    @Inherited
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Ignore {
    }

    /**
     * Scan for all classes in the same package as the test class, excluding anonymous classes, lombok builders,
     * the compilation unit under test, and finally any classes which have been annotated with {@link Ignore}.
     * @param testClass the test class
     * @return classes matching criteria
     */
    public static Stream<Class<?>> findClassesFor(AbstractSourceCompilingTest testClass) {
        return findClasses(testClass.getClass().getPackage().getName())
                .filter(clazz -> clazz.getAnnotation(Ignore.class) == null)
                .filter(clazz -> !isLombokBuilder(clazz))
                .filter(clazz -> !clazz.isAnonymousClass())
                .filter(clazz -> !clazz.equals(testClass.getCompilationUnitUnderTest()));
    }

    private static boolean isLombokBuilder(Class<?> clazz) {
        if (clazz.getSimpleName().endsWith("Builder")) {
            String[] parts = clazz.getCanonicalName().split("\\.");
            int len = parts.length;
            return len >= 2 && parts[len - 1].replaceAll("Builder$", "").equals(parts[len - 2]);
        } else {
            return false;
        }
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
