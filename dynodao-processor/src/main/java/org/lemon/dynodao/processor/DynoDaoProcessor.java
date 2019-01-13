package org.lemon.dynodao.processor;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.DynoDao;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.generate.PojoTypeSpecFactory;
import org.lemon.dynodao.processor.index.DynamoIndex;
import org.lemon.dynodao.processor.index.DynamoIndexParser;
import org.lemon.dynodao.processor.index.IndexType;
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
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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
            Set<DynamoIndex> indexes = dynamoIndexParser.getIndexes(document);

            List<PojoClassBuilder> leafPojos = getLeafPojos(document, indexes);
            Set<TypeSpec> leafPojoTypes = toTypeSpecs(leafPojos);

            List<PojoClassBuilder> certainIndexTrunkPojos = getCertainIndexTrunkPojos(document, indexes, leafPojoTypes);
            Set<TypeSpec> certainIndexTrunkPojoTypes = toTypeSpecs(certainIndexTrunkPojos);

            typeSpecWriter.writeAll(document, leafPojoTypes);
            typeSpecWriter.writeAll(document, certainIndexTrunkPojoTypes);
        }
    }

    private Set<TypeSpec> toTypeSpecs(Collection<PojoClassBuilder> pojos) {
        return pojos.stream()
                .map(pojoTypeSpecFactory::build)
                .collect(toSet());
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

    private List<PojoClassBuilder> getCertainIndexTrunkPojos(TypeElement document, Set<DynamoIndex> indexes, Set<TypeSpec> leafPojoTypes) {
        return indexes.stream()
                .filter(index -> !index.getIndexType().equals(IndexType.LOCAL_SECONDARY_INDEX))
                .filter(index -> IndexLengthType.lengthOf(index).equals(IndexLengthType.RANGE))
                .filter(index -> !isAmbiguousIndexHashKey(index, indexes))
                .map(index -> {
                    PojoClassBuilder pojo = new PojoClassBuilder(document);
                    pojo.setIndex(index, IndexLengthType.HASH);
                    leafPojoTypes.stream()
                            .filter(leaf -> leaf.fieldSpecs.containsAll(pojo.getFields()))
                            .forEach(pojo::addWither);
                    return pojo;
                })
                .collect(toList());
    }

    private Set<DynamoIndex> getAmbiguousHashKeyIndexes(DynamoIndex index, Set<DynamoIndex> indexes) {
        Stream<DynamoIndex> stream = indexes.stream()
                .filter(i -> !i.equals(index))
                .filter(i -> i.getHashKey().equals(index.getHashKey()));
        if (!index.getIndexType().equals(IndexType.GLOBAL_SECONDARY_INDEX)) {
            stream = stream.filter(i -> i.getIndexType().equals(IndexType.GLOBAL_SECONDARY_INDEX));
        }
        return stream.collect(toSet());
    }

    private boolean isAmbiguousIndexHashKey(DynamoIndex index, Set<DynamoIndex> indexes) {
        return !getAmbiguousHashKeyIndexes(index, indexes).isEmpty();
    }

}
