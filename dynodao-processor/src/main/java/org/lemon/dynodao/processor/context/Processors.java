package org.lemon.dynodao.processor.context;

import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Arrays;

/**
 * Provides access to {@link Elements} and {@link Types} utility classes, as well as providing some additional
 * utility methods related to them.
 */
public class Processors implements Elements, Types {

    @Delegate(types = Elements.class)
    private final Elements elementUtils;

    @Delegate(types = Types.class)
    private final Types typeUtils;

    @Inject Processors(ProcessorContext processorContext) {
        this.elementUtils = processorContext.getProcessingEnvironment().getElementUtils();
        this.typeUtils = processorContext.getProcessingEnvironment().getTypeUtils();
    }

    /**
     * Returns the {@link TypeElement} corresponding to the {@link Class} provided.
     * @param clazz the class to get the type of
     * @return the {@link TypeElement}
     * @see Elements#getTypeElement(CharSequence)
     */
    public TypeElement getTypeElement(Class<?> clazz) {
        return getTypeElement(clazz.getCanonicalName());
    }

    /**
     * Returns the {@link DeclaredType} of the <tt>type</tt> with the given <tt>typeArguments</tt>.
     * @param type the class type
     * @param typeArguments the template arguments, if any
     * @return the {@link DeclaredType}
     * @see Types#getDeclaredType(TypeElement, TypeMirror...)
     */
    public DeclaredType getDeclaredType(Class<?> type, Class<?>... typeArguments) {
        TypeElement typeElement = getTypeElement(type.getCanonicalName());
        TypeMirror[] args = Arrays.stream(typeArguments)
                .map(clazz -> getTypeElement(clazz.getCanonicalName()).asType())
                .toArray(TypeMirror[]::new);
        return getDeclaredType(typeElement, args);
    }

    /**
     * Returns <tt>true</tt> if the <tt>typeMirror</tt> is the same type as the declared type
     * constructed by the Class arguments.
     * @param typeMirror the type to test
     * @param type the declared class type
     * @param typeArguments the template arguments of the declared class type, if any
     * @return <tt>true</tt> if <tt>typeMirror</tt> is the same declared type as the classes provided
     * @see Types#isSameType(TypeMirror, TypeMirror)
     * @see #getDeclaredType(Class, Class[])
     */
    public boolean isSameType(TypeMirror typeMirror, Class<?> type, Class<?>... typeArguments) {
        return isSameType(typeMirror, getDeclaredType(type, typeArguments));
    }

    /**
     * Returns the method element enclosed in the <tt>typeElement</tt> whose simple name is <tt>methodName</tt>.
     * @param typeElement the type to find a method in
     * @param methodName the method name to find
     * @return the method
     * @throws IllegalArgumentException if the method does not exist
     */
    public ExecutableElement getMethodByName(TypeElement typeElement, String methodName) {
        return typeElement.getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.METHOD))
                .filter(method -> method.getSimpleName().contentEquals(methodName))
                .map(method -> (ExecutableElement) method)
                .findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("no such method [%s] in [%s]", methodName, typeElement)));
    }

}
