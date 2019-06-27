package com.github.dynodao.processor.schema.parse;

import com.github.dynodao.processor.util.StreamUtil.Streamable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Contains all {@link SchemaParser} implementations in priority order.
 */
public class SchemaParsers implements Streamable<SchemaParser> {

    @Inject DocumentSchemaParser documentSchemaParser;
    @Inject ListTypeSchemaParser listTypeSchemaParser;
    @Inject MapTypeSchemaParser mapTypeSchemaParser;
    @Inject BooleanTypeSchemaParser booleanTypeSchemaParser;
    @Inject CharacterTypeSchemaParser characterTypeSchemaParser;
    @Inject StringTypeSchemaParser stringTypeSchemaParser;
    @Inject NumericTypeSchemaParser numericTypeSchemaParser;
    @Inject NullSchemaParser nullSchemaParser;

    private final List<SchemaParser> schemaParsers = new ArrayList<>();

    @Inject SchemaParsers() { }

    /**
     * Priority order:
     * <ul>
     *  <li>Element parse overrides.
     *  <li>Type parse overrides.
     *  <li>Built in parsing.
     *  <li>Null parse.
     * </ul>
     */
    @Inject void initSchemaParsers() {
        schemaParsers.add(documentSchemaParser);

        schemaParsers.add(listTypeSchemaParser);
        schemaParsers.add(mapTypeSchemaParser);
        schemaParsers.add(booleanTypeSchemaParser);
        schemaParsers.add(characterTypeSchemaParser);
        schemaParsers.add(stringTypeSchemaParser);
        schemaParsers.add(numericTypeSchemaParser);

        schemaParsers.add(nullSchemaParser);
    }

    @Override
    public Iterator<SchemaParser> iterator() {
        return schemaParsers.iterator();
    }

}
