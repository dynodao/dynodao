package org.dynodao.processor;

import com.google.auto.service.AutoService;
import org.dynodao.annotation.DynoDaoSchema;
import org.dynodao.processor.context.ProcessorContext;
import org.dynodao.processor.context.ProcessorMessager;
import org.dynodao.processor.schema.DynamoSchema;
import org.dynodao.processor.schema.DynamoSchemaParser;
import org.dynodao.processor.schema.index.DynamoIndex;
import org.dynodao.processor.serialize.SerializerTypeSpec;
import org.dynodao.processor.serialize.SerializerTypeSpecFactory;
import org.dynodao.processor.stage.KeyLengthType;
import org.dynodao.processor.stage.Stage;
import org.dynodao.processor.stage.StageTypeSpec;
import org.dynodao.processor.stage.StageTypeSpecFactory;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * The annotation processor for {@link DynoDaoSchema}.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.dynodao.annotation.DynoDaoSchema")
public class DynoDaoProcessor extends AbstractProcessor {

    @Inject ProcessorContext processorContext;
    @Inject ProcessorMessager processorMessager;
    @Inject DynamoSchemaParser dynamoSchemaParser;
    @Inject SerializerTypeSpecFactory serializerTypeSpecFactory;
    @Inject StageTypeSpecFactory stageTypeSpecFactory;
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
            processElements(elementsToProcess);
            processorMessager.emitMessages();
        }
        return false;
    }

    private Set<TypeElement> findElementsToProcess(Set<? extends TypeElement> annotations) {
        return annotations.stream()
                .map(processorContext.getRoundEnvironment()::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .filter(element -> element.getKind().equals(ElementKind.CLASS))
                .map(element -> (TypeElement) element)
                .collect(toSet());
    }

    private void processElements(Set<TypeElement> elements) {
        for (TypeElement documentElement : elements) {
            try {
                processDocumentElement(documentElement);
            } catch (RuntimeException e) {
                processorMessager.submitError("DynoDaoProcessor had uncaught exception: %s\n"
                        + "DynoDao tries to continue processing even when it enters an error state in an effort to report all errors, check for others!", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void processDocumentElement(TypeElement documentElement) {
        DynamoSchema schema = dynamoSchemaParser.parse(documentElement);
        SerializerTypeSpec serializer = serializerTypeSpecFactory.build(schema);

        List<BuiltTypeSpec> builtTypes = new ArrayList<>();
        builtTypes.add(serializer);

        Stage stagedBuilder = new Stage(schema, serializer);
        for (DynamoIndex index : schema.getIndexes()) {
            KeyLengthType keyLengthType = KeyLengthType.lengthOf(index);

            Optional<StageTypeSpec> indexRangeKeyPojo = getIndexRangeKeyStage(schema, serializer, index, keyLengthType);
            StageTypeSpec indexHashKeyPojo = getIndexHashKeyStage(schema, serializer, index, indexRangeKeyPojo);
            StageTypeSpec indexPojo = getIndexStage(schema, serializer, index, indexHashKeyPojo);

            stagedBuilder.addUser(indexPojo);

            indexRangeKeyPojo.ifPresent(builtTypes::add);
            builtTypes.add(indexHashKeyPojo);
            builtTypes.add(indexPojo);
        }
        builtTypes.add(toTypeSpec(stagedBuilder));
        typeSpecWriter.writeAll(builtTypes);
    }

    private Optional<StageTypeSpec> getIndexRangeKeyStage(DynamoSchema schema, SerializerTypeSpec serializer, DynamoIndex index, KeyLengthType keyLengthType) {
        if (keyLengthType.equals(KeyLengthType.RANGE)) {
            Stage pojo = new Stage(schema, serializer)
                    .withIndex(index, keyLengthType);
            return Optional.of(toTypeSpec(pojo));
        } else {
            return Optional.empty();
        }
    }

    private StageTypeSpec getIndexHashKeyStage(DynamoSchema schema, SerializerTypeSpec serializer, DynamoIndex index, Optional<StageTypeSpec> indexRangeKeyStage) {
        Stage pojo = new Stage(schema, serializer)
                .withIndex(index, KeyLengthType.HASH);
        indexRangeKeyStage.ifPresent(pojo::addWither);
        return toTypeSpec(pojo);
    }

    private StageTypeSpec getIndexStage(DynamoSchema schema, SerializerTypeSpec serializer, DynamoIndex index, StageTypeSpec indexHashKeyStage) {
        Stage pojo = new Stage(schema, serializer)
                .withIndex(index, KeyLengthType.NONE)
                .addWither(indexHashKeyStage);
        return toTypeSpec(pojo);
    }

    private StageTypeSpec toTypeSpec(Stage pojo) {
        return stageTypeSpecFactory.build(pojo);
    }

}
