package org.lemon.dynodao.processor.serialize;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;
import org.lemon.dynodao.processor.context.Processors;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Houses contextual data for serializing values into {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}s.
 */
@ToString(exclude = "processors")
@EqualsAndHashCode(exclude = "processors")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SerializationContext {

    @Getter private final TypeElement document;
    private final List<Marshaller> marshallers = new ArrayList<>();
    private final Processors processors;

    /**
     * TODO this doesn't really support {@link org.lemon.dynodao.annotation.DynoDaoValueMapped}, which is not at a type level
     */
    @Value
    private static class Marshaller {
        private final TypeMirror type;
        private final MarshallMethod method;
    }

    /**
     * @param type the type
     * @return <tt>true</tt> if the context already knows how to serialize the type, <tt>false</tt> otherwise
     */
    public boolean hasMarshallerForType(TypeMirror type) {
        return getMarshallMethodForType(type) != null;
    }

    /**
     * Returns the method which can marshall the type into AttributeValue, or <tt>null</tt> if the type
     * is not able to be marshalled.
     * @param type the type
     * @return the method which serializes the type, or <tt>null</tt> if no such method exists
     */
    public MarshallMethod getMarshallMethodForType(TypeMirror type) {
        return marshallers.stream()
                .filter(marshaller -> processors.isSameType(type, marshaller.getType()))
                .map(Marshaller::getMethod)
                .findAny().orElse(null);
    }

    /**
     * Adds a new method to the context which serializes the type.
     * @param type the type which the method serializes
     * @param method the serialization method
     */
    void addMarshaller(TypeMirror type, MarshallMethod method) {
        marshallers.add(new Marshaller(type, method));
    }

    /**
     * @return all serialization methods
     */
    List<MarshallMethod> getAllMarshallMethods() {
        return marshallers.stream()
                .map(Marshaller::getMethod)
                .collect(toList());
    }

}
