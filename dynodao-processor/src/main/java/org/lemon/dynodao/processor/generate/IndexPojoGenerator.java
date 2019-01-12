package org.lemon.dynodao.processor.generate;

import static java.util.stream.Collectors.toSet;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.index.DynamoIndex;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Generates the {@link org.lemon.dynodao.DocumentLoad} and {@link org.lemon.dynodao.DocumentQuery} pojos.
 */
public class IndexPojoGenerator {

    @Inject ProcessorContext processorContext;

    @Inject TwoFieldPojoGenerator twoFieldPojoGenerator;

    @Inject IndexPojoGenerator() { }

    public Set<TypeSpec> buildPojos(TypeElement document, Set<DynamoIndex> indexes) {
        Set<TypeSpec> types = new HashSet<>();

        Set<DynamoIndex> twoFieldIndexes = indexes.stream()
                .filter(index -> index.getRangeKey().isPresent())
                .collect(toSet());

        twoFieldIndexes.forEach(index -> types.add(twoFieldPojoGenerator.build(document, index)));

        return types;
    }

}
