package com.github.dynodao.processor.serialize.generate;

import com.github.dynodao.processor.util.StreamUtil.Streamable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores all of the implementations of {@link SerializerTypeSpecMutator} in the appropriate order.
 */
public class SerializerTypeSpecMutators implements Streamable<SerializerTypeSpecMutator> {

    @Inject GeneratedAnnotationSerializerTypeSpecMutator generatedAnnotationSerializerTypeSpecMutator;
    @Inject JavaDocSerializerTypeSpecMutator javaDocSerializerTypeSpecMutator;
    @Inject ModifiersSerializerTypeSpecMutator modifiersSerializerTypeSpecMutator;
    @Inject CtorSerializerTypeSpecMutator ctorSerializerTypeSpecMutator;
    @Inject ItemSerializationMethodSerializerTypeSpecMutator itemSerializationMethodSerializerTypeSpecMutator;
    @Inject ItemDeserializationMethodSerializerTypeSpecMutator itemDeserializationMethodSerializerTypeSpecMutator;
    @Inject SerializationMethodsSerializerTypeSpecMutator serializationMethodsSerializerTypeSpecMutator;
    @Inject DeserializationMethodsSerializerTypeSpecMutator deserializationMethodsSerializerTypeSpecMutator;

    private final List<SerializerTypeSpecMutator> serializerTypeSpecMutators = new ArrayList<>();

    @Inject SerializerTypeSpecMutators() { }

    @Inject void initSerializerTypeSpecMutators() {
        serializerTypeSpecMutators.add(generatedAnnotationSerializerTypeSpecMutator);
        serializerTypeSpecMutators.add(javaDocSerializerTypeSpecMutator);
        serializerTypeSpecMutators.add(modifiersSerializerTypeSpecMutator);
        serializerTypeSpecMutators.add(ctorSerializerTypeSpecMutator);

        serializerTypeSpecMutators.add(itemSerializationMethodSerializerTypeSpecMutator);
        serializerTypeSpecMutators.add(itemDeserializationMethodSerializerTypeSpecMutator);

        serializerTypeSpecMutators.add(serializationMethodsSerializerTypeSpecMutator);
        serializerTypeSpecMutators.add(deserializationMethodsSerializerTypeSpecMutator);
    }

    @Override
    public Iterator<SerializerTypeSpecMutator> iterator() {
        return serializerTypeSpecMutators.iterator();
    }

}
