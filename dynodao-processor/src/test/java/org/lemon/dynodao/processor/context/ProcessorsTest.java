package org.lemon.dynodao.processor.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;
import org.mockito.Mock;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ProcessorsTest extends AbstractUnitTest {

    @Mock private TypeMirror typeMirrorMock;
    @Mock private Element elementMock;
    @Mock private DeclaredType declaredTypeMock;
    @Mock private TypeElement typeElementMock;
    @Mock private TypeElement typeElementAsTypeArgMock;
    @Mock private PrimitiveType primitiveTypeMock;
    @Mock private ArrayType arrayTypeMock;
    @Mock private NoType noTypeMock;
    @Mock private NullType nullTypeMock;
    @Mock private WildcardType wildcardTypeMock;
    @Mock private ExecutableType executableTypeMock;
    @Mock private AnnotationMirror annotationMirrorMock;
    @Mock private Name nameMock;
    @Mock private ExecutableElement executableElementMock;
    @Mock private AnnotationValue annotationValueMock;
    @Mock private PackageElement packageElementMock;
    @Mock private Writer writerMock;

    @Mock private ProcessingEnvironment processingEnvironmentMock;

    @Mock private Elements elementsMock;
    @Mock private Types typesMock;

    private Processors classUnderTest;

    @BeforeEach
    void setup() {
        ProcessorContext context = new ProcessorContext(processingEnvironmentMock);
        when(processingEnvironmentMock.getElementUtils()).thenReturn(elementsMock);
        when(processingEnvironmentMock.getTypeUtils()).thenReturn(typesMock);
        classUnderTest = new Processors(context);
    }

    @AfterEach
    void verifyNoMoreUtilsInteractions() {
        verifyNoMoreInteractions(typesMock, elementsMock);
    }

    @Test
    void getTypeElement_class_returnsTypeByCanonicalName() {
        String canonicalName = ProcessorsTest.class.getCanonicalName();
        when(elementsMock.getTypeElement(canonicalName)).thenReturn(typeElementMock);
        TypeElement typeElement = classUnderTest.getTypeElement(ProcessorsTest.class);
        assertThat(typeElement).isEqualTo(typeElementMock);
        verify(elementsMock).getTypeElement(canonicalName);
    }

    @Test
    void getDeclaredType_classes_returnsTypeByCanonicalNames() {
        String canonicalName = ProcessorsTest.class.getCanonicalName();
        String typeArg = Object.class.getCanonicalName();
        when(elementsMock.getTypeElement(canonicalName)).thenReturn(typeElementMock);
        when(elementsMock.getTypeElement(typeArg)).thenReturn(typeElementAsTypeArgMock);
        when(typeElementAsTypeArgMock.asType()).thenReturn(typeMirrorMock);
        when(typesMock.getDeclaredType(typeElementMock, typeMirrorMock)).thenReturn(declaredTypeMock);
        DeclaredType declaredType = classUnderTest.getDeclaredType(ProcessorsTest.class, Object.class);
        assertThat(declaredType).isEqualTo(declaredTypeMock);
        verify(elementsMock).getTypeElement(canonicalName);
        verify(elementsMock).getTypeElement(typeArg);
        verify(typesMock).getDeclaredType(typeElementMock, typeMirrorMock);
    }

    @Test
    void isSameType_classes_comparesTypeByCanonicalNames() {
        String canonicalName = ProcessorsTest.class.getCanonicalName();
        String typeArg = Object.class.getCanonicalName();
        when(elementsMock.getTypeElement(canonicalName)).thenReturn(typeElementMock);
        when(elementsMock.getTypeElement(typeArg)).thenReturn(typeElementAsTypeArgMock);
        when(typeElementAsTypeArgMock.asType()).thenReturn(typeMirrorMock);
        when(typesMock.getDeclaredType(typeElementMock, typeMirrorMock)).thenReturn(declaredTypeMock);
        when(typesMock.isSameType(typeMirrorMock, declaredTypeMock)).thenReturn(true);
        boolean isSameType = classUnderTest.isSameType(typeMirrorMock, ProcessorsTest.class, Object.class);
        assertThat(isSameType).isTrue();
        verify(elementsMock).getTypeElement(canonicalName);
        verify(elementsMock).getTypeElement(typeArg);
        verify(typesMock).getDeclaredType(typeElementMock, typeMirrorMock);
        verify(typesMock).isSameType(typeMirrorMock, declaredTypeMock);
    }

    @Test
    void getMethodByName_methodExists_returnsExecutableElement() {
        doReturn(singletonList(executableElementMock)).when(typeElementMock).getEnclosedElements();
        when(executableElementMock.getKind()).thenReturn(ElementKind.METHOD);
        when(executableElementMock.getSimpleName()).thenReturn(nameMock);
        when(nameMock.contentEquals("methodName")).thenReturn(true);
        ExecutableElement method = classUnderTest.getMethodByName(typeElementMock, "methodName");
        assertThat(method).isEqualTo(executableElementMock);
    }

    @Test
    void getMethodByName_noMethodByName_throwsIllegalArgumentException() {
        doReturn(singletonList(executableElementMock)).when(typeElementMock).getEnclosedElements();
        when(executableElementMock.getKind()).thenReturn(ElementKind.METHOD);
        when(executableElementMock.getSimpleName()).thenReturn(nameMock);
        when(nameMock.contentEquals("differentMethodName")).thenReturn(true);
        assertThatThrownBy(() -> classUnderTest.getMethodByName(typeElementMock, "methodName"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getMethodByName_noMethods_throwsIllegalArgumentException() {
        doReturn(singletonList(elementMock)).when(typeElementMock).getEnclosedElements();
        when(elementMock.getKind()).thenReturn(ElementKind.FIELD);
        assertThatThrownBy(() -> classUnderTest.getMethodByName(typeElementMock, "methodName"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * {@link Types} delegate
     */

    @Test
    void ctor_onlyUseCase_pullsUtilsFromProcessingEnvironment() {
        // ctor invoked in setup()
        verify(processingEnvironmentMock).getElementUtils();
        verify(processingEnvironmentMock).getTypeUtils();
        verifyNoMoreInteractions(processingEnvironmentMock);
    }

    @Test
    void asElement_delegate_returnsAsElement() {
        when(typesMock.asElement(typeMirrorMock)).thenReturn(elementMock);
        Element element = classUnderTest.asElement(typeMirrorMock);
        assertThat(element).isEqualTo(elementMock);
        verify(typesMock).asElement(typeMirrorMock);
    }

    @Test
    void asMemberOf_delegate_returnsAsMemberOf() {
        when(typesMock.asMemberOf(declaredTypeMock, elementMock)).thenReturn(typeMirrorMock);
        TypeMirror typeMirror = classUnderTest.asMemberOf(declaredTypeMock, elementMock);
        assertThat(typeMirror).isEqualTo(typeMirrorMock);
        verify(typesMock).asMemberOf(declaredTypeMock, elementMock);
    }

    @Test
    void boxedClass_delegate_returnsBoxedClass() {
        when(typesMock.boxedClass(primitiveTypeMock)).thenReturn(typeElementMock);
        TypeElement typeElement = classUnderTest.boxedClass(primitiveTypeMock);
        assertThat(typeElement).isEqualTo(typeElementMock);
        verify(typesMock).boxedClass(primitiveTypeMock);
    }

    @Test
    void capture_delegate_returnsCapture() {
        when(typesMock.capture(typeMirrorMock)).thenReturn(typeMirrorMock);
        TypeMirror typeMirror = classUnderTest.capture(typeMirrorMock);
        assertThat(typeMirror).isEqualTo(typeMirrorMock);
        verify(typesMock).capture(typeMirrorMock);
    }

    @Test
    void contains_delegate_returnsContains() {
        when(typesMock.contains(typeMirrorMock, typeMirrorMock)).thenReturn(true);
        boolean contains = classUnderTest.contains(typeMirrorMock, typeMirrorMock);
        assertThat(contains).isTrue();
        verify(typesMock).contains(typeMirrorMock, typeMirrorMock);
    }

    @Test
    void directSupertypes_delegate_returnsDirectSupertypes() {
        doReturn(singletonList(typeMirrorMock)).when(typesMock).directSupertypes(typeMirrorMock);
        List<? extends TypeMirror> supertypes = classUnderTest.directSupertypes(typeMirrorMock);
        assertThat(supertypes).isEqualTo(singletonList(typeMirrorMock));
        verify(typesMock).directSupertypes(typeMirrorMock);
    }

    @Test
    void erasure_delegate_returnsErasure() {
        when(typesMock.erasure(typeMirrorMock)).thenReturn(typeMirrorMock);
        TypeMirror erasure = classUnderTest.erasure(typeMirrorMock);
        assertThat(erasure).isEqualTo(typeMirrorMock);
        verify(typesMock).erasure(typeMirrorMock);
    }

    @Test
    void getArrayType_delegate_returnsGetArrayType() {
        when(typesMock.getArrayType(typeMirrorMock)).thenReturn(arrayTypeMock);
        ArrayType arrayType = classUnderTest.getArrayType(typeMirrorMock);
        assertThat(arrayType).isEqualTo(arrayTypeMock);
        verify(typesMock).getArrayType(typeMirrorMock);
    }

    @Test
    void getDeclaredType_delegateWithContaining_returnsGetDeclaredType() {
        when(typesMock.getDeclaredType(declaredTypeMock, typeElementMock, typeMirrorMock)).thenReturn(declaredTypeMock);
        DeclaredType declaredType = classUnderTest.getDeclaredType(declaredTypeMock, typeElementMock, typeMirrorMock);
        assertThat(declaredType).isEqualTo(declaredTypeMock);
        verify(typesMock).getDeclaredType(declaredTypeMock, typeElementMock, typeMirrorMock);
    }

    @Test
    void getDeclaredType_delegate_returnsGetDeclaredType() {
        when(typesMock.getDeclaredType(typeElementMock, typeMirrorMock)).thenReturn(declaredTypeMock);
        DeclaredType declaredType = classUnderTest.getDeclaredType(typeElementMock, typeMirrorMock);
        assertThat(declaredType).isEqualTo(declaredTypeMock);
        verify(typesMock).getDeclaredType(typeElementMock, typeMirrorMock);
    }

    @Test
    void getNoType_delegate_returnsGetNoType() {
        when(typesMock.getNoType(TypeKind.NONE)).thenReturn(noTypeMock);
        NoType noType = classUnderTest.getNoType(TypeKind.NONE);
        assertThat(noType).isEqualTo(noTypeMock);
        verify(typesMock).getNoType(TypeKind.NONE);
    }

    @Test
    void getNullType_delegate_returnsGetNullType() {
        when(typesMock.getNullType()).thenReturn(nullTypeMock);
        NullType nullType = classUnderTest.getNullType();
        assertThat(nullType).isEqualTo(nullTypeMock);
        verify(typesMock).getNullType();
    }

    @Test
    void getPrimitiveType_delegate_returnsGetPrimitiveType() {
        when(typesMock.getPrimitiveType(TypeKind.NONE)).thenReturn(primitiveTypeMock);
        PrimitiveType primitiveType = classUnderTest.getPrimitiveType(TypeKind.NONE);
        assertThat(primitiveType).isEqualTo(primitiveTypeMock);
        verify(typesMock).getPrimitiveType(TypeKind.NONE);
    }

    @Test
    void getWildcardType_delegate_returnsGetWildcardType() {
        when(typesMock.getWildcardType(typeMirrorMock, typeMirrorMock)).thenReturn(wildcardTypeMock);
        WildcardType wildcardType = classUnderTest.getWildcardType(typeMirrorMock, typeMirrorMock);
        assertThat(wildcardType).isEqualTo(wildcardTypeMock);
        verify(typesMock).getWildcardType(typeMirrorMock, typeMirrorMock);
    }

    @Test
    void isAssignable_delegate_returnsIsAssignable() {
        when(typesMock.isAssignable(typeMirrorMock, typeMirrorMock)).thenReturn(true);
        boolean isAssignable = classUnderTest.isAssignable(typeMirrorMock, typeMirrorMock);
        assertThat(isAssignable).isTrue();
        verify(typesMock).isAssignable(typeMirrorMock, typeMirrorMock);
    }

    @Test
    void isSameType_delegate_returnsIsSameType() {
        when(typesMock.isSameType(typeMirrorMock, typeMirrorMock)).thenReturn(true);
        boolean isSameType = classUnderTest.isSameType(typeMirrorMock, typeMirrorMock);
        assertThat(isSameType).isTrue();
        verify(typesMock).isSameType(typeMirrorMock, typeMirrorMock);
    }

    @Test
    void isSubsignature_delegate_returnsIsSubsignature() {
        when(typesMock.isSubsignature(executableTypeMock, executableTypeMock)).thenReturn(true);
        boolean isSubsignature = classUnderTest.isSubsignature(executableTypeMock, executableTypeMock);
        assertThat(isSubsignature).isTrue();
        verify(typesMock).isSubsignature(executableTypeMock, executableTypeMock);
    }

    @Test
    void isSubtype_delegate_returnsIsSubtype() {
        when(typesMock.isSubtype(typeMirrorMock, typeMirrorMock)).thenReturn(true);
        boolean isSubtype = classUnderTest.isSubtype(typeMirrorMock, typeMirrorMock);
        assertThat(isSubtype).isTrue();
        verify(typesMock).isSubtype(typeMirrorMock, typeMirrorMock);
    }

    @Test
    void unboxedType_delegate_returnsUnboxedType() {
        when(typesMock.unboxedType(typeMirrorMock)).thenReturn(primitiveTypeMock);
        PrimitiveType unboxedType = classUnderTest.unboxedType(typeMirrorMock);
        assertThat(unboxedType).isEqualTo(primitiveTypeMock);
        verify(typesMock).unboxedType(typeMirrorMock);
    }

    /**
     * {@link Elements} delegate
     */

    @Test
    void getAllAnnotationMirrors_delegate_returnsGetAllAnnotationMirrors() {
        doReturn(singletonList(annotationMirrorMock)).when(elementsMock).getAllAnnotationMirrors(elementMock);
        List<? extends AnnotationMirror> annotationMirrors = classUnderTest.getAllAnnotationMirrors(elementMock);
        assertThat(annotationMirrors).isEqualTo(singletonList(annotationMirrorMock));
        verify(elementsMock).getAllAnnotationMirrors(elementMock);
    }

    @Test
    void getAllMembers_delegate_returnGetAllMembers() {
        doReturn(singletonList(elementMock)).when(elementsMock).getAllMembers(typeElementMock);
        List<? extends Element> members = classUnderTest.getAllMembers(typeElementMock);
        assertThat(members).isEqualTo(singletonList(elementMock));
        verify(elementsMock).getAllMembers(typeElementMock);
    }

    @Test
    void getBinaryName_delegate_returnsGetBinaryName() {
        when(elementsMock.getBinaryName(typeElementMock)).thenReturn(nameMock);
        Name binaryName = classUnderTest.getBinaryName(typeElementMock);
        assertThat(binaryName).isEqualTo(nameMock);
        verify(elementsMock).getBinaryName(typeElementMock);
    }

    @Test
    void getConstantExpression_delegate_returnsGetConstantExpression() {
        Object value = new Object();
        when(elementsMock.getConstantExpression(value)).thenReturn("getConstantExpression");
        String constantExpression = classUnderTest.getConstantExpression(value);
        assertThat(constantExpression).isEqualTo("getConstantExpression");
        verify(elementsMock).getConstantExpression(value);
    }

    @Test
    void getDocComment_delegate_returnsGetDocComment() {
        when(elementsMock.getDocComment(elementMock)).thenReturn("getDocComment");
        String docComment = classUnderTest.getDocComment(elementMock);
        assertThat(docComment).isEqualTo("getDocComment");
        verify(elementsMock).getDocComment(elementMock);
    }

    @Test
    void getElementValuesWithDefaults_delegate_returnGetElementValuesWithDefaults() {
        doReturn(singletonMap(executableElementMock, annotationValueMock)).when(elementsMock).getElementValuesWithDefaults(annotationMirrorMock);
        Map<? extends ExecutableElement,? extends AnnotationValue> elementValues = classUnderTest.getElementValuesWithDefaults(annotationMirrorMock);
        assertThat(elementValues).isEqualTo(singletonMap(executableElementMock, annotationValueMock));
        verify(elementsMock).getElementValuesWithDefaults(annotationMirrorMock);
    }

    @Test
    void getName_delegate_returnsGetName() {
        when(elementsMock.getName("name")).thenReturn(nameMock);
        Name name = classUnderTest.getName("name");
        assertThat(name).isEqualTo(nameMock);
        verify(elementsMock).getName("name");
    }

    @Test
    void getPackageElement_delegate_returnsGetPackageElement() {
        when(elementsMock.getPackageElement("package")).thenReturn(packageElementMock);
        PackageElement packageElement = classUnderTest.getPackageElement("package");
        assertThat(packageElement).isEqualTo(packageElementMock);
        verify(elementsMock).getPackageElement("package");
    }

    @Test
    void getPackageOf_delegate_returnsGetPackageOf() {
        when(elementsMock.getPackageOf(elementMock)).thenReturn(packageElementMock);
        PackageElement packageElement = classUnderTest.getPackageOf(elementMock);
        assertThat(packageElement).isEqualTo(packageElementMock);
        verify(elementsMock).getPackageOf(elementMock);
    }

    @Test
    void getTypeElement_delegate_returnsGetTypeElement() {
        when(elementsMock.getTypeElement("name")).thenReturn(typeElementMock);
        TypeElement typeElement = classUnderTest.getTypeElement("name");
        assertThat(typeElement).isEqualTo(typeElementMock);
        verify(elementsMock).getTypeElement("name");
    }

    @Test
    void hides_delegate_returnsHides() {
        when(elementsMock.hides(elementMock, elementMock)).thenReturn(true);
        boolean hides = classUnderTest.hides(elementMock, elementMock);
        assertThat(hides).isTrue();
        verify(elementsMock).hides(elementMock, elementMock);
    }

    @Test
    void isDeprecated_delegate_returnsIsDeprecated() {
        when(elementsMock.isDeprecated(elementMock)).thenReturn(true);
        boolean isDeprecated = classUnderTest.isDeprecated(elementMock);
        assertThat(isDeprecated).isTrue();
        verify(elementsMock).isDeprecated(elementMock);
    }

    @Test
    void isFunctionalInterface_delegate_returnsIsFunctionalInterface() {
        when(elementsMock.isFunctionalInterface(typeElementMock)).thenReturn(true);
        boolean isFunctionalInterface = classUnderTest.isFunctionalInterface(typeElementMock);
        assertThat(isFunctionalInterface).isTrue();
        verify(elementsMock).isFunctionalInterface(typeElementMock);
    }

    @Test
    void overrides_delegate_returnsOverrides() {
        when(elementsMock.overrides(executableElementMock, executableElementMock, typeElementMock)).thenReturn(true);
        boolean overrides = classUnderTest.overrides(executableElementMock, executableElementMock, typeElementMock);
        assertThat(overrides).isTrue();
        verify(elementsMock).overrides(executableElementMock, executableElementMock, typeElementMock);
    }

    @Test
    void printElements_delegate_printsElements() {
        classUnderTest.printElements(writerMock, elementMock);
        verify(elementsMock).printElements(writerMock, elementMock);
    }

}
