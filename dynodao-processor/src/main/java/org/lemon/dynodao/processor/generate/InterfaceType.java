package org.lemon.dynodao.processor.generate;

import static org.lemon.dynodao.processor.generate.GenerateConstants.PAGINATED_LIST;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.lemon.dynodao.DocumentLoad;
import org.lemon.dynodao.DocumentQuery;
import org.lemon.dynodao.processor.context.ProcessorContext;

import com.google.common.collect.Iterables;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
enum InterfaceType {

    DOCUMENT_LOAD(DocumentLoad.class) {
        @Override
        TypeName asReturnType(TypeElement document, ProcessorContext processorContext) {
            return TypeName.get(document.asType());
        }
    },

    DOCUMENT_QUERY(DocumentQuery.class) {
        @Override
        TypeName asReturnType(TypeElement document, ProcessorContext processorContext) {
            return ParameterizedTypeName.get(PAGINATED_LIST, TypeName.get(document.asType()));
        }
    };

    private final Class<?> clazz;

    TypeElement getTypeElement(ProcessorContext processorContext) {
        return processorContext.getElementUtils().getTypeElement(clazz.getCanonicalName());
    }

    String getInterfaceName() {
        return clazz.getSimpleName();
    }

    ParameterizedTypeName asSuperInterface(TypeElement document, ProcessorContext processorContext) {
        return ParameterizedTypeName.get(ClassName.get(getTypeElement(processorContext)), TypeName.get(document.asType()));
    }

    ExecutableElement getMethod(ProcessorContext processorContext) {
        return (ExecutableElement) Iterables.getOnlyElement(getTypeElement(processorContext).getEnclosedElements());
    }

    abstract TypeName asReturnType(TypeElement document, ProcessorContext processorContext);

}
