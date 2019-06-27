package com.github.dynodao.processor.itest.serialization.document;

import com.github.dynodao.annotation.DynoDaoAttribute;
import com.github.dynodao.annotation.DynoDaoDocument;
import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoIgnore;
import com.github.dynodao.annotation.DynoDaoSchema;
import com.github.dynodao.processor.test.PackageScanner;
import lombok.Data;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    /**
     * {@code boolean} needs a specific attribute, as the getter is <tt>isAttribute</tt>.
     */
    private boolean bool;
    private Boolean boolObj;

    /**
     * We should only have to test against one other nested type as the serializer delegates to other methods
     * which will be tested by other classes.
     */
    @DynoDaoAttribute("attribute")
    private String dynamoNameIsAttribute;

    private Document document;

    @DynoDaoIgnore
    private Object ifNotIgnoredThisFailsToCompile;

}

@Data
@DynoDaoDocument
@PackageScanner.Ignore
class Document {

    private String attribute1;
    private String attribute2;

    @DynoDaoAttribute("attribute3")
    private String dynamoNameIsAttribute3;

    @DynoDaoIgnore
    private Object ifNotIgnoredThisFailsToCompile;

}
