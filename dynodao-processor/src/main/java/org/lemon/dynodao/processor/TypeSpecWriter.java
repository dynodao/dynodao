package org.lemon.dynodao.processor;

import com.squareup.javapoet.JavaFile;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.context.ProcessorMessager;
import org.lemon.dynodao.processor.context.Processors;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Creates files specified by {@link com.squareup.javapoet.TypeSpec}s.
 */
class TypeSpecWriter {

    private final Processors processors;
    private final ProcessorMessager processorMessager;
    private final ProcessorContext processorContext;

    @Inject TypeSpecWriter(Processors processors, ProcessorMessager processorMessager, ProcessorContext processorContext) {
        this.processors = processors;
        this.processorMessager = processorMessager;
        this.processorContext = processorContext;
    }

    /**
     * Writes all of the type specs to file.
     * @param builtTypeSpecs all of the types the document creates
     */
    void writeAll(Iterable<BuiltTypeSpec> builtTypeSpecs) {
        builtTypeSpecs.forEach(this::write);
    }

    /**
     * Writes a single type spec to file.
     * @param builtTypeSpec a type the annotated class creates
     */
    void write(BuiltTypeSpec builtTypeSpec) {
        if (!processorMessager.hasErrors()) {
            JavaFile file = JavaFile.builder(getPackage(builtTypeSpec.getDocument()), builtTypeSpec.getTypeSpec())
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

    private String getPackage(TypeElement document) {
        return processors.getPackageOf(document).getQualifiedName().toString();
    }

}
