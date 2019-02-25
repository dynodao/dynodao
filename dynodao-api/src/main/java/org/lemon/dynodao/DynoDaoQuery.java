package org.lemon.dynodao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;

import java.util.stream.Stream;

public interface DynoDaoQuery<T> {

    Stream<T> query(AmazonDynamoDB amazonDynamoDb);

    QueryRequest asRequest();

}
