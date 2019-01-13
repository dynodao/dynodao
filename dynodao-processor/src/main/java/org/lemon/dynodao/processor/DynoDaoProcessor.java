package org.lemon.dynodao.processor;

import static java.util.stream.Collectors.toSet;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.DynoDao;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.generate.PojoTypeSpecFactory;
import org.lemon.dynodao.processor.index.DynamoIndex;
import org.lemon.dynodao.processor.index.DynamoIndexParser;
import org.lemon.dynodao.processor.model.IndexLengthType;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The annotation processor for {@link org.lemon.dynodao.DynoDao}.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.lemon.dynodao.*")
public class DynoDaoProcessor extends AbstractProcessor {

    private ProcessorContext processorContext;

    @Inject DynamoIndexParser dynamoIndexParser;

    @Inject PojoTypeSpecFactory pojoTypeSpecFactory;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        processorContext = new ProcessorContext(processingEnv);
        DaggerObjectGraph.builder()
                .contextModule(new ContextModule(processorContext))
                .build().inject(this);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processorContext.newRound(roundEnv);
        if (!roundEnv.processingOver()) {
            Set<TypeElement> elementsToProcess = findElementsToProcess(annotations);
            if (!elementsToProcess.isEmpty()) {
                processElements(elementsToProcess);
            }
            processorContext.processMessages();
        }
        return false;
    }

    private Set<TypeElement> findElementsToProcess(Set<? extends TypeElement> annotations) {
        return processorContext.getRoundEnvironment().getElementsAnnotatedWith(DynoDao.class).stream()
                .filter(element -> element.getKind().equals(ElementKind.CLASS))
                .map(element -> (TypeElement) element)
                .collect(toSet());
    }

    private void processElements(Set<TypeElement> elements) {
        for (TypeElement document : elements) {
            Set<DynamoIndex> indexes = dynamoIndexParser.getIndexes(document);

            List<PojoClassBuilder> pojos = getPojos(document, indexes);
            Set<TypeSpec> types = pojos.stream()
                    .map(pojoTypeSpecFactory::build)
                    .collect(toSet());

            types.forEach(type -> write(document, type));
        }
    }

    private List<PojoClassBuilder> getPojos(TypeElement document, Set<DynamoIndex> indexes) {
        List<PojoClassBuilder> pojos = new ArrayList<>();
        pojos.addAll(getLeafPojos(document, indexes));
        return pojos;
    }

    private List<PojoClassBuilder> getLeafPojos(TypeElement document, Set<DynamoIndex> indexes) {
        List<PojoClassBuilder> pojos = new ArrayList<>();
        for (DynamoIndex index : indexes) {
            PojoClassBuilder pojo = new PojoClassBuilder(document);
            pojo.setIndex(index, IndexLengthType.lengthOf(index));
            pojos.add(pojo);
        }
        return pojos;
    }

    private void write(TypeElement document, TypeSpec type) {
        JavaFile file = JavaFile.builder(getDynoDaoPackageName(document), type)
                .indent("    ")
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
