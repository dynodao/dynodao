package com.github.dynodao.processor.schema;

import com.github.dynodao.annotation.DynoDaoSchema;
import com.github.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.github.dynodao.processor.schema.attribute.NullDynamoAttribute;
import com.github.dynodao.processor.schema.index.DynamoIndex;
import com.github.dynodao.processor.schema.index.DynamoIndexParsers;
import com.github.dynodao.processor.schema.parse.SchemaParsers;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Parses a schema {@link TypeElement} into a {@link DynamoSchema}.
 */
public class DynamoSchemaParser {

    private final SchemaParsers schemaParsers;
    private final DynamoIndexParsers indexParsers;

    @Inject DynamoSchemaParser(SchemaParsers schemaParsers, DynamoIndexParsers indexParsers) {
        this.schemaParsers = schemaParsers;
        this.indexParsers = indexParsers;
    }

    /**
     * Returns the dynamo schema the document object represents.
     * If a parse is impossible, attributes are mapped to {@link NullDynamoAttribute}
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
        SchemaContext context = new SchemaContext(schemaParsers);
        DynamoAttribute document = context.parseAttribute(documentElement, documentElement.asType(), "");
        // the element should always be a document, just cast it
        return (DocumentDynamoAttribute) document;
    }

    private Set<DynamoIndex> getIndexes(DocumentDynamoAttribute document) {
        Set<DynamoIndex> indexes = new LinkedHashSet<>();
        indexParsers.forEach(parser -> indexes.addAll(parser.getIndexesFrom(document)));
        return indexes;
    }

}
