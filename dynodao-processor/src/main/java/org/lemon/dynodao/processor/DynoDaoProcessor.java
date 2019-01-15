package org.lemon.dynodao.processor;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
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
            Set<DynamoIndex> indexes = dynamoIndexParser.getIndexes(document);

            List<PojoClassBuilder> twoFieldIndexPojos = getTwoFieldIndexPojos(document, indexes);
            Set<PojoTypeSpec> twoFieldIndexPojoTypes = toTypeSpecs(twoFieldIndexPojos);

            List<PojoClassBuilder> oneFieldIndexPojos = getOneFieldIndexPojos(document, indexes, twoFieldIndexPojoTypes);
            Set<PojoTypeSpec> oneFieldIndexPojoTypes = toTypeSpecs(oneFieldIndexPojos);

            List<PojoClassBuilder> zeroFieldIndexPojos = getZeroFieldIndexPojos(document, indexes, oneFieldIndexPojoTypes);
            Set<PojoTypeSpec> zeroFieldIndexPojoTypes = toTypeSpecs(zeroFieldIndexPojos);

            List<PojoClassBuilder> stagedBuilderPojos = getStagedBuilderPojos(document, indexes, zeroFieldIndexPojoTypes);
            Set<PojoTypeSpec> stagedBuilderPojoTypes = toTypeSpecs(stagedBuilderPojos);

            typeSpecWriter.writeAll(document, twoFieldIndexPojoTypes);
            typeSpecWriter.writeAll(document, oneFieldIndexPojoTypes);
            typeSpecWriter.writeAll(document, zeroFieldIndexPojoTypes);
            typeSpecWriter.writeAll(document, stagedBuilderPojoTypes);
        }
    }

    private List<PojoClassBuilder> getTwoFieldIndexPojos(TypeElement document, Set<DynamoIndex> indexes) {
        return indexes.stream()
                .filter(index -> IndexLengthType.lengthOf(index).equals(IndexLengthType.RANGE))
                .map(index -> new PojoClassBuilder(document).withIndex(index, IndexLengthType.lengthOf(index)))
                .collect(toList());
    }

    private List<PojoClassBuilder> getOneFieldIndexPojos(TypeElement document, Set<DynamoIndex> indexes, Set<PojoTypeSpec> twoFieldIndexPojoTypes) {
        return indexes.stream()
                .map(index -> new PojoClassBuilder(document)
                        .withIndex(index, IndexLengthType.HASH)
                        .addApplicableWithers(twoFieldIndexPojoTypes))
                .collect(toList());
    }

    private List<PojoClassBuilder> getZeroFieldIndexPojos(TypeElement document, Set<DynamoIndex> indexes, Set<PojoTypeSpec> oneFieldIndexPojoTypes) {
        return indexes.stream()
                .map(index -> new PojoClassBuilder(document)
                        .withIndex(index, IndexLengthType.NONE)
                        .addApplicableWithers(oneFieldIndexPojoTypes))
                .collect(toList());
    }

    private List<PojoClassBuilder> getStagedBuilderPojos(TypeElement document, Set<DynamoIndex> indexes, Set<PojoTypeSpec> zeroFieldIndexPojoTypes) {
        return singletonList(new PojoClassBuilder(document).addApplicableUsers(zeroFieldIndexPojoTypes));
    }

    private Set<PojoTypeSpec> toTypeSpecs(Collection<PojoClassBuilder> pojos) {
        return pojos.stream()
                .map(pojoTypeSpecFactory::build)
                .collect(toSet());
    }

}
