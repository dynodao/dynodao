package org.lemon.dynodao.processor.schema;

import org.lemon.dynodao.annotation.DynoDaoSchema;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;
import org.lemon.dynodao.processor.schema.index.DynamoIndex;
import org.lemon.dynodao.processor.schema.index.DynamoIndexParsers;
import org.lemon.dynodao.processor.schema.parse.SchemaParsers;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Parses a schema {@link TypeElement} into a {@link DynamoSchema}.
 */
public class DynamoSchemaParser {

    private final Processors processors;
    private final SchemaParsers schemaParsers;
    private final DynamoIndexParsers indexParsers;

    @Inject DynamoSchemaParser(Processors processors, SchemaParsers schemaParsers, DynamoIndexParsers indexParsers) {
        this.processors = processors;
        this.schemaParsers = schemaParsers;
        this.indexParsers = indexParsers;
    }

    /**
     * Returns the dynamo schema the document object represents.
     * If a parse is impossible, attributes are mapped to {@link org.lemon.dynodao.processor.schema.attribute.NullDynamoAttribute}
     * and compile errors are submitted.
     * @param documentElement the schema document type
     */
    public DynamoSchema parse(TypeElement documentElement) {
        String tableName = getTableName(documentElement);
        DocumentDynamoAttribute document = parseDocument(documentElement);
        Set<DynamoIndex> indexes = getIndexes(document);
        return DynamoSchema.builder()
                .tableName(tableName)
                .document(document)
                .indexes(indexes)
                .build();
    }

    private String getTableName(TypeElement documentElement) {
        return documentElement.getAnnotation(DynoDaoSchema.class).tableName();
    }

    private DocumentDynamoAttribute parseDocument(TypeElement documentElement) {
        SchemaContext context = new SchemaContext(processors, schemaParsers);
        DynamoAttribute document = context.parseAttribute(documentElement, documentElement.asType(), "", context);
        // the element should always be a document, just cast it
        return (DocumentDynamoAttribute) document;
    }

    private Set<DynamoIndex> getIndexes(DocumentDynamoAttribute document) {
        Set<DynamoIndex> indexes = new LinkedHashSet<>();
        indexParsers.forEach(parser -> indexes.addAll(parser.getIndexesFrom(document)));
        return indexes;
    }

}
