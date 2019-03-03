package org.lemon.dynodao.processor.itest.serialization.document;

import lombok.Data;
import org.lemon.dynodao.annotation.DynoDaoAttribute;
import org.lemon.dynodao.annotation.DynoDaoDocument;
import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    /**
     * We should only have to test against one nested type as the serializer delegates to other methods
     * which will be tested by other classes.
     */
    @DynoDaoAttribute("attribute")
    private String dynamoNameIsAttribute;

    private Document document;

}

@Data
@DynoDaoDocument
class Document {

    private String attribute1;
    private String attribute2;

    @DynoDaoAttribute("attribute3")
    private String dynamoNameIsAttribute3;

}
