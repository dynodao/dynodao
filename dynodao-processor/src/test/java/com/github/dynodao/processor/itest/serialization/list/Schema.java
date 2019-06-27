package com.github.dynodao.processor.itest.serialization.list;

import lombok.Data;
import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoSchema;
import com.github.dynodao.processor.test.PackageScanner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    // -- typical java lists --
    private List<String> list;
    private ArrayList<String> arrayList;
    private LinkedList<String> linkedList;
    private CopyOnWriteArrayList<String> copyOnWriteArrayList;

    // -- old garbo list types --
    private Vector<String> vector;

    // -- list type with no type args --
    private NoTypeArgsList noTypeArgsList;

}

@PackageScanner.Ignore
class NoTypeArgsList extends ArrayList<String> {
}
