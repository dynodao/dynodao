package org.lemon.dynodao.processor;

import com.google.auto.service.AutoService;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.context.ProcessorMessager;
import org.lemon.dynodao.processor.node.KeyLengthType;
import org.lemon.dynodao.processor.node.NodeClassData;
import org.lemon.dynodao.processor.node.NodeTypeSpec;
import org.lemon.dynodao.processor.node.NodeTypeSpecFactory;
import org.lemon.dynodao.processor.schema.DynamoSchema;
import org.lemon.dynodao.processor.schema.DynamoSchemaParser;
import org.lemon.dynodao.processor.schema.index.DynamoIndex;
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
 * The annotation processor for {@link org.lemon.dynodao.annotation.DynoDaoSchema}.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.lemon.dynodao.annotation.DynoDaoSchema")
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
        for (TypeElement documentElement : elements) {
            try {
                processDocumentElement(documentElement);
            } catch (RuntimeException e) {
                processorMessager.submitError("DynoDaoProcessor had uncaught exception: %s\nDynoDao carries on even if it finds errors, check for others!", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void processDocumentElement(TypeElement documentElement) {
        DynamoSchema schema = dynamoSchemaParser.parse(documentElement);
        SerializerTypeSpec serializer = serializerTypeSpecFactory.build(schema);

        List<BuiltTypeSpec> builtTypes = new ArrayList<>();
        builtTypes.add(serializer);

        NodeClassData stagedBuilder = new NodeClassData(schema, serializer);
        for (DynamoIndex index : schema.getIndexes()) {
            KeyLengthType keyLengthType = KeyLengthType.lengthOf(index);

            Optional<NodeTypeSpec> indexRangeKeyPojo = getIndexRangeKeyNode(schema, serializer, index, keyLengthType);
            NodeTypeSpec indexHashKeyPojo = getIndexHashKeyNode(schema, serializer, index, indexRangeKeyPojo);
            NodeTypeSpec indexPojo = getIndexNode(schema, serializer, index, indexHashKeyPojo);

            stagedBuilder.addUser(indexPojo);

            indexRangeKeyPojo.ifPresent(builtTypes::add);
            builtTypes.add(indexHashKeyPojo);
            builtTypes.add(indexPojo);
        }
        builtTypes.add(toTypeSpec(stagedBuilder));
        typeSpecWriter.writeAll(builtTypes);
    }

    private Optional<NodeTypeSpec> getIndexRangeKeyNode(DynamoSchema schema, SerializerTypeSpec serializer, DynamoIndex index, KeyLengthType keyLengthType) {
        if (keyLengthType.equals(KeyLengthType.RANGE)) {
            NodeClassData pojo = new NodeClassData(schema, serializer)
                    .withIndex(index, keyLengthType);
            return Optional.of(toTypeSpec(pojo));
        } else {
            return Optional.empty();
        }
    }

    private NodeTypeSpec getIndexHashKeyNode(DynamoSchema schema, SerializerTypeSpec serializer, DynamoIndex index, Optional<NodeTypeSpec> indexRangeKeyPojo) {
        NodeClassData pojo = new NodeClassData(schema, serializer)
                .withIndex(index, KeyLengthType.HASH);
        indexRangeKeyPojo.ifPresent(pojo::addWither);
        return toTypeSpec(pojo);
    }

    private NodeTypeSpec getIndexNode(DynamoSchema schema, SerializerTypeSpec serializer, DynamoIndex index, NodeTypeSpec indexHashKeyPojo) {
        NodeClassData pojo = new NodeClassData(schema, serializer)
                .withIndex(index, KeyLengthType.NONE)
                .addWither(indexHashKeyPojo);
        return toTypeSpec(pojo);
    }

    private NodeTypeSpec toTypeSpec(NodeClassData pojo) {
        return nodeTypeSpecFactory.build(pojo);
    }



}
