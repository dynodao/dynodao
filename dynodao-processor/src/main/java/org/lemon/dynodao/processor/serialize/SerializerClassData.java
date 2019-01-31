package org.lemon.dynodao.processor.serialize;

import lombok.Value;

import javax.lang.model.element.TypeElement;
import java.util.List;

/**
 * Data model for a serializer utility class to generate.
 */
@Value
public class SerializerClassData {

    private final TypeElement document;
    private final SerializationContext serializationContext;

    /**
     * @return all serialization methods
     */
    public List<MarshallMethod> getAllMarshallMethods() {
        return serializationContext.getAllMarshallMethods();
    }

    /**
     * @return all deserialization methods
     */
    public List<UnmarshallMethod> getAllUnmarshallMethods() {
        return serializationContext.getAllUnmarshallMethods();
    }

}
