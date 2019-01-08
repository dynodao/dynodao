package org.lemon.dynodao.playground.query;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.lemon.dynodao.playground.Model;

public interface ModelLoad {

    Model load(DynamoDBMapper dynamoDbMapper);
}
