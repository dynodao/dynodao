package org.lemon.dynodao.processor.generate;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.index.DynamoIndex;
import org.lemon.dynodao.processor.index.IndexType;

class TwoFieldPojoGenerator {

    @Inject ProcessorContext processorContext;

    @Inject TwoFieldPojoGenerator() { }

    TypeSpec build(TypeElement document, DynamoIndex index) {
        InterfaceType interfaceType = getInterfaceType(index);
        return TypeSpec.classBuilder(getClassName(document, index, interfaceType))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(interfaceType.asSuperInterface(document, processorContext))
                .addMethod(buildCtor(document, index))
                .addMethod( buildGetMethod(document, index, interfaceType))
                .build();
    }

    private InterfaceType getInterfaceType(DynamoIndex index) {
        return index.getIndexType().equals(IndexType.TABLE) ? InterfaceType.DOCUMENT_LOAD : InterfaceType.DOCUMENT_QUERY;
    }

    private String getClassName(TypeElement document, DynamoIndex index, InterfaceType interfaceType) {
        VariableElement hash = index.getHashKey();
        VariableElement range = index.getRangeKey().get();
        return String.format("%s%s%s%s", capitalize(hash), capitalize(range), document.getSimpleName(), interfaceType.getInterfaceName());
    }

    private String capitalize(Element element) {
        return capitalize(element.getSimpleName().toString());
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private MethodSpec buildCtor(TypeElement document, DynamoIndex index) {
        VariableElement hash = index.getHashKey();
        VariableElement range = index.getRangeKey().get();
        return MethodSpec.constructorBuilder()
                .addParameter(TypeName.get(hash.asType()), hash.getSimpleName().toString())
                .addParameter(TypeName.get(range.asType()), range.getSimpleName().toString())
                .addStatement("this.$L = $L", hash, hash)
                .addStatement("this.$L = $L", range, range)
                .build();
    }

    private MethodSpec buildGetMethod(TypeElement document, DynamoIndex index, InterfaceType interfaceType) {
        VariableElement hash = index.getHashKey();
        VariableElement range = index.getRangeKey().get();
        ExecutableElement method = interfaceType.getMethod(processorContext);
        ParameterSpec dynamoDbMapper = ParameterSpec.builder(DynamoDBMapper.class, "dynamoDbMapper").build();
        MethodSpec.Builder load = MethodSpec.methodBuilder(method.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(interfaceType.asReturnType(document, processorContext))
                .addParameter(dynamoDbMapper);

        if (interfaceType.equals(InterfaceType.DOCUMENT_LOAD)) {
            load.addStatement("return $N.load($T.class, $L, $L)", dynamoDbMapper, document.asType(), hash, range);
        } else if (interfaceType.equals(InterfaceType.DOCUMENT_QUERY)) {
            TypeName queryExpression = ParameterizedTypeName.get(ClassName.get(DynamoDBQueryExpression.class), TypeName.get(document.asType()));
            load
                    .addStatement("$T query = new $T()", queryExpression, queryExpression)
                    .addStatement("query.setIndexName($S)", index.getName())
                    .addStatement("query.setKeyConditionExpression($S)", String.format("#%s = :%s AND #%s = :%s", hash, hash, range, range))
                    .addStatement("query.addExpressionAttributeNamesEntry($S, $S)", "#" + hash, hash)
                    .addStatement("query.addExpressionAttributeNamesEntry($S, $S)", "#" + range, range)
                    // TODO need to select the proper withX method here
                    // https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/HowItWorks.CoreComponents.html#HowItWorks.CoreComponents.TablesItemsAttributes
                    // The only data types allowed for primary key attributes are string, number, or binary. 
                    .addStatement("query.addExpressionAttributeValuesEntry($S, new $T().withS($L))", ":" + hash, AttributeValue.class, hash)
                    .addStatement("query.addExpressionAttributeValuesEntry($S, new $T().withS($L))", ":" + range, AttributeValue.class, range)
                    .addStatement("return $N.query($T.class, query)", dynamoDbMapper, document.asType());
        }
        return load.build();
    }

}
