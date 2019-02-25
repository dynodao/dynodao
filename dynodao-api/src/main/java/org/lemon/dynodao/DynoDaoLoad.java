package org.lemon.dynodao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;

import java.util.stream.Stream;

public interface DynoDaoLoad<T> {

    Stream<T> load(AmazonDynamoDB amazonDynamoDb);

    GetItemRequest asRequest();

}
