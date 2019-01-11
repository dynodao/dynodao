package org.lemon.dynodao.processor.generate;

import static org.lemon.dynodao.processor.util.StringUtil.capitalize;

import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.lemon.dynodao.processor.index.DynamoIndex;
import org.lemon.dynodao.processor.index.IndexType;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FullIndexDocumentQuery {

    private final TypeElement document;
    private final DynamoIndex index;

    InterfaceType getInterfaceType() {
        return index.getIndexType().equals(IndexType.TABLE) ? InterfaceType.DOCUMENT_LOAD : InterfaceType.DOCUMENT_QUERY;
    }

    public String getClassName() {
        if (index.getRangeKey().isPresent()) {
            return String.format("%s%s%s%s", capitalize(index.getHashKey()), capitalize(index.getRangeKey().get()), document.getSimpleName(), getInterfaceType().getInterfaceName());
        } else {
            return String.format("%s%s%s", capitalize(index.getHashKey()), document.getSimpleName(), getInterfaceType().getInterfaceName());
        }
    }

    public List<VariableElement> getIndexFields() {
        if (index.getRangeKey().isPresent()) {
            return Arrays.asList(index.getHashKey());
        } else {
            return Arrays.asList(index.getHashKey(), index.getRangeKey().get());
        }
    }

}
