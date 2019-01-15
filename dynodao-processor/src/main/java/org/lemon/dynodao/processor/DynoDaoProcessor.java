package org.lemon.dynodao.processor;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import org.lemon.dynodao.DynoDao;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.generate.PojoTypeSpecFactory;
import org.lemon.dynodao.processor.index.DynamoIndex;
import org.lemon.dynodao.processor.index.DynamoIndexParser;
import org.lemon.dynodao.processor.model.IndexLengthType;
import org.lemon.dynodao.processor.model.PojoClassBuilder;
import org.lemon.dynodao.processor.model.PojoTypeSpec;

import com.google.auto.service.AutoService;

/**
 * The annotation processor for {@link org.lemon.dynodao.DynoDao}.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.lemon.dynodao.*")
public class DynoDaoProcessor extends AbstractProcessor {

    @Inject ProcessorContext processorContext;
    @Inject DynamoIndexParser dynamoIndexParser;
    @Inject PojoTypeSpecFactory pojoTypeSpecFactory;
    @Inject TypeSpecWriter typeSpecWriter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        DaggerObjectGraph.builder()
                .contextModule(new ContextModule(new ProcessorContext(processingEnv)))
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
            List<PojoTypeSpec> pojos = new ArrayList<>();
            PojoClassBuilder stagedBuilder = new PojoClassBuilder(document);
            for (DynamoIndex index : dynamoIndexParser.getIndexes(document)) {
                IndexLengthType indexLengthType = IndexLengthType.lengthOf(index);

                Optional<PojoTypeSpec> indexRangeKeyPojo = getIndexRangeKeyPojo(document, index, indexLengthType);
                PojoTypeSpec indexHashKeyPojo = getIndexHashKeyPojo(document, index, indexRangeKeyPojo);
                PojoTypeSpec indexPojo = getIndexPojo(document, index, indexHashKeyPojo);

                stagedBuilder.addUser(indexPojo);

                indexRangeKeyPojo.ifPresent(pojos::add);
                pojos.add(indexHashKeyPojo);
                pojos.add(indexPojo);
            }
            pojos.add(toTypeSpec(stagedBuilder));
            typeSpecWriter.writeAll(pojos);
        }
    }

    private Optional<PojoTypeSpec> getIndexRangeKeyPojo(TypeElement document, DynamoIndex index, IndexLengthType indexLengthType) {
        if (indexLengthType.equals(IndexLengthType.RANGE)) {
            PojoClassBuilder pojo = new PojoClassBuilder(document).withIndex(index, indexLengthType);
            return Optional.of(toTypeSpec(pojo));
        } else {
            return Optional.empty();
        }
    }

    private PojoTypeSpec getIndexHashKeyPojo(TypeElement document, DynamoIndex index, Optional<PojoTypeSpec> indexRangeKeyPojo) {
        PojoClassBuilder pojo = new PojoClassBuilder(document).withIndex(index, IndexLengthType.HASH);
        indexRangeKeyPojo.ifPresent(pojo::addWither);
        return toTypeSpec(pojo);
    }

    private PojoTypeSpec getIndexPojo(TypeElement document, DynamoIndex index, PojoTypeSpec indexHashKeyPojo) {
        PojoClassBuilder pojo = new PojoClassBuilder(document)
                .withIndex(index, IndexLengthType.NONE)
                .addWither(indexHashKeyPojo);
        return toTypeSpec(pojo);
    }

    private PojoTypeSpec toTypeSpec(PojoClassBuilder pojo) {
        return pojoTypeSpecFactory.build(pojo);
    }

}
