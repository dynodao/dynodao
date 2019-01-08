package org.lemon.dynodao.playground;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.lemon.dynodao.playground.query.ModelQuery;
import org.lemon.dynodao.playground.query.ModelLoad;

public class ModelDynoDao {

    private final DynamoDBMapper dynamoDbMapper;

    public ModelDynoDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
    }

    public Model save(Model model) {
        return model;
    }

    public Model get(ModelLoad modelLoad) {
        return modelLoad.load(dynamoDbMapper);
    }

    public List<Model> get(ModelQuery modelQuery) {
        return modelQuery.query(dynamoDbMapper);
    }

}
