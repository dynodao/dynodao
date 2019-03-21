package org.dynodao.processor.itest.serialization.document;

import lombok.Data;
import org.dynodao.annotation.DynoDaoAttribute;
import org.dynodao.annotation.DynoDaoDocument;
import org.dynodao.annotation.DynoDaoHashKey;
import org.dynodao.annotation.DynoDaoIgnore;
import org.dynodao.annotation.DynoDaoSchema;
import org.dynodao.processor.test.PackageScanner;

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
