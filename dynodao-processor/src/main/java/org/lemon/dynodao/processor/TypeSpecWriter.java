package org.lemon.dynodao.processor;

import com.squareup.javapoet.JavaFile;
import org.lemon.dynodao.DynoDao;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.context.ProcessorMessage;
import org.lemon.dynodao.processor.context.ProcessorMessager;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.model.PojoTypeSpec;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Creates files specified by {@link com.squareup.javapoet.TypeSpec}s.
 */
class TypeSpecWriter {

    @Inject Processors processors;
    @Inject ProcessorMessager processorMessager;
    @Inject ProcessorContext processorContext;

    @Inject TypeSpecWriter() { }

    /**
     * Writes all of the type specs to file.
     * @param pojoTypeSpecs all of the types the document creates
     */
    void writeAll(Iterable<PojoTypeSpec> pojoTypeSpecs) {
        pojoTypeSpecs.forEach(this::write);
    }

    /**
     * Writes a single type spec to file.
     * @param pojoTypeSpec a type the annotated class creates
     */
    void write(PojoTypeSpec pojoTypeSpec) {
        if (!processorMessager.hasErrors()) {
            JavaFile file = JavaFile.builder(getDynoDaoPackageName(pojoTypeSpec.getPojo().getDocument()), pojoTypeSpec.getTypeSpec())
                    .indent("    ")
                    .skipJavaLangImports(true)
                    .build();
            try {
                file.writeTo(processorContext.getProcessingEnvironment().getFiler());
            } catch (IOException e) {
                throw new UncheckedIOException(String.format("got IOException when writing file\n%s", file), e);
            }
        }
    }

    private String getDynoDaoPackageName(TypeElement document) {
        String packageName = document.getAnnotation(DynoDao.class).implPackage();
        if (packageName.isEmpty()) {
            return processors.getPackageOf(document).getQualifiedName().toString();
        } else {
            return packageName;
        }
    }

}
