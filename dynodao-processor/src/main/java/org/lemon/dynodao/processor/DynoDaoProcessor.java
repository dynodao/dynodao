package org.lemon.dynodao.processor;

import com.google.auto.service.AutoService;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.context.ProcessorMessager;
import org.lemon.dynodao.processor.dynamo.DynamoIndex;
import org.lemon.dynodao.processor.dynamo.DynamoSchemaParser;
import org.lemon.dynodao.processor.dynamo.DynamoStructuredSchema;
import org.lemon.dynodao.processor.node.IndexLengthType;
import org.lemon.dynodao.processor.node.NodeClassData;
import org.lemon.dynodao.processor.node.NodeTypeSpec;
import org.lemon.dynodao.processor.node.NodeTypeSpecFactory;
import org.lemon.dynodao.processor.serialize.SerializerTypeSpec;
import org.lemon.dynodao.processor.serialize.SerializerTypeSpecFactory;

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
 * The annotation processor for {@link org.lemon.dynodao.DynoDao}.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.lemon.dynodao.*")
public class DynoDaoProcessor extends AbstractProcessor {

    @Inject ProcessorContext processorContext;
    @Inject ProcessorMessager processorMessager;
    @Inject DynamoSchemaParser dynamoSchemaParser;
    @Inject SerializerTypeSpecFactory serializerTypeSpecFactory;
    @Inject NodeTypeSpecFactory nodeTypeSpecFactory;
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
        for (TypeElement document : elements) {
            try {
                processDocument(document);
            } catch (RuntimeException e) {
                processorMessager.submitError("DynoDaoProcessor had uncaught exception: %s\nDynoDao carries on even if it finds errors, check for others!", e.getMessage());
            }
        }
    }

    private void processDocument(TypeElement document) {
        DynamoStructuredSchema schema = dynamoSchemaParser.getSchema(document);
        SerializerTypeSpec serializer = serializerTypeSpecFactory.build(document, schema);

        List<BuiltTypeSpec> builtTypes = new ArrayList<>();
        builtTypes.add(serializer);

        NodeClassData stagedBuilder = new NodeClassData(document, serializer);
        for (DynamoIndex index : schema.getIndexes()) {
            IndexLengthType indexLengthType = IndexLengthType.lengthOf(index);

            Optional<NodeTypeSpec> indexRangeKeyPojo = getIndexRangeKeyNode(document, serializer, index, indexLengthType);
            NodeTypeSpec indexHashKeyPojo = getIndexHashKeyNode(document, serializer, index, indexRangeKeyPojo);
            NodeTypeSpec indexPojo = getIndexNode(document, serializer, index, indexHashKeyPojo);

            stagedBuilder.addUser(indexPojo);

            indexRangeKeyPojo.ifPresent(builtTypes::add);
            builtTypes.add(indexHashKeyPojo);
            builtTypes.add(indexPojo);
        }
        builtTypes.add(toTypeSpec(stagedBuilder));
        typeSpecWriter.writeAll(builtTypes);
    }

    private Optional<NodeTypeSpec> getIndexRangeKeyNode(TypeElement document, SerializerTypeSpec serializer, DynamoIndex index, IndexLengthType indexLengthType) {
        if (indexLengthType.equals(IndexLengthType.RANGE)) {
            NodeClassData pojo = new NodeClassData(document, serializer).withIndex(index, indexLengthType);
            return Optional.of(toTypeSpec(pojo));
        } else {
            return Optional.empty();
        }
    }

    private NodeTypeSpec getIndexHashKeyNode(TypeElement document, SerializerTypeSpec serializer, DynamoIndex index, Optional<NodeTypeSpec> indexRangeKeyPojo) {
        NodeClassData pojo = new NodeClassData(document, serializer).withIndex(index, IndexLengthType.HASH);
        indexRangeKeyPojo.ifPresent(pojo::addWither);
        return toTypeSpec(pojo);
    }

    private NodeTypeSpec getIndexNode(TypeElement document, SerializerTypeSpec serializer, DynamoIndex index, NodeTypeSpec indexHashKeyPojo) {
        NodeClassData pojo = new NodeClassData(document, serializer)
                .withIndex(index, IndexLengthType.NONE)
                .addWither(indexHashKeyPojo);
        return toTypeSpec(pojo);
    }

    private NodeTypeSpec toTypeSpec(NodeClassData pojo) {
        return nodeTypeSpecFactory.build(pojo);
    }

}
