package org.lemon.dynodao.processor;

import java.io.IOException;
import java.io.UncheckedIOException;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;

import org.lemon.dynodao.DynoDao;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.model.PojoTypeSpec;

import com.squareup.javapoet.JavaFile;

/**
 * Creates files specified by {@link com.squareup.javapoet.TypeSpec}s.
 */
class TypeSpecWriter {

    @Inject ProcessorContext processorContext;

    @Inject TypeSpecWriter() { }

    /**
     * Writes all of the type specs to file.
     * @param document the type annotated with {@link DynoDao}
     * @param pojoTypeSpecs all of the types the document creates
     */
    void writeAll(TypeElement document, Iterable<PojoTypeSpec> pojoTypeSpecs) {
        pojoTypeSpecs.forEach(typeSpec -> write(document, typeSpec));
    }

    /**
     * Writes a single type spec to file.
     * @param document the type annotated with {@link DynoDao}
     * @param pojoTypeSpec a type the annotated class creates
     */
    void write(TypeElement document, PojoTypeSpec pojoTypeSpec) {
        JavaFile file = JavaFile.builder(getDynoDaoPackageName(document), pojoTypeSpec.getTypeSpec())
                .indent("    ")
                .skipJavaLangImports(true)
                .build();
        try {
            file.writeTo(processorContext.getFiler());
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("got IOException when writing file\n%s", file), e);
        }
    }

    private String getDynoDaoPackageName(TypeElement document) {
        DynoDao dynoDao = document.getAnnotation(DynoDao.class);
        String packageName = dynoDao.implPackage();
        if (packageName == null || packageName.isEmpty()) {
            return processorContext.getElementUtils().getPackageOf(document).toString();
        } else {
            return packageName;
        }
    }

}
