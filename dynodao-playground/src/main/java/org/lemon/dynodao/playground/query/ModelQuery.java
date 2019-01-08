package org.lemon.dynodao.playground.query;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import org.lemon.dynodao.playground.Model;

public interface ModelQuery {

    PaginatedList<Model> query(DynamoDBMapper dynamoDbMapper);
}
