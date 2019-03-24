# dynodao
[![Build Status](https://travis-ci.org/dynodao/dynodao.svg?branch=master)](https://travis-ci.org/dynodao/dynodao)
[![codecov](https://codecov.io/gh/dynodao/dynodao/branch/master/graph/badge.svg)](https://codecov.io/gh/dynodao/dynodao)
```
taras@me:~$ dynodao
 ________________________________________
/ An annotation processor for generating \
| fluent, type-safe DynamoDB             |
\ interactions.                          /
 ----------------------------------------
\                             .       .
 \                           / `.   .' " 
  \                  .---.  <    > <    >  .---.
   \                 |    \  \ - ~ ~ - /  /    |
         _____          ..-~             ~-..-~
        |     |   \~~~\.'                    `./~~~/
       ---------   \__/                        \__/
      .'  O    \     /               /       \  " 
     (_____,    `._.'               |         }  \/~~~/
      `----.          /       }     |        /    \__/
            `-.      |       /      |       /      `. ,~~|
                ~-.__|      /_ - ~ ^|      /- _      `..-'   
                     |     /        |     /     ~-.     `-. _  _  _
                     |_____|        |_____|         ~ - . _ _ _ _ _>
```

Puns aside, the dinosaur wearing a top hat is spot on. Working with the DynamoDb Java SDK can tedious. The addition of DynamoDBMapper removes much of the tedium by using magic to serialize and deserialize classes, but [working with expressions](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBMapper.QueryScanExample.html) is still awkward, and all that magic (read: reflection) incurs a runtime cost.

Dynodao takes a lot of inspiration from DynamoDBMapper. Compared to it, dynodao offers:

* **Fast execution** by using plain methods instead of reflective access
* **Compile-time type safety** wherever possible
  * The generated classes are specific to a *schema* which only contain operations allowed on the defined attributes and types
  * If an object cannot be automatically mapped to an `AttributeValue`, you get a compilation error
* **Easily debuggable code** as there is no magic at runtime
* **Real lazy pagination** as `PaginatedList` end up [storing all results](https://github.com/puppetlabs/aws-sdk-for-java/blob/master/src/main/java/com/amazonaws/services/dynamodb/datamodeling/PaginatedList.java#L121) from a read operation in memory

To generate all of the classes, you must define a *schema* class. This is just an annotated POJO (aka java-bean) which corresponds to the data you'd store in Dynamo. This class must adhear to the [java-beans](https://en.wikipedia.org/wiki/JavaBeans) specification, ie needs a default contructor, and getters and setters.

```java
@DynoDaoSchema(tableName = "things")
class MySchema {
    @DynoDaoHashKey
    private String hashKey;
    
    @DynoDaoRangeKey
    private String rangeKey;

    // default ctor, getters, setters omitted...
}
```

The processor generates several classes for simplifying the interactions with a DynamoDB table which stores items of type `MySchema`. Depending on the schema, many classes may be generated, but you don't really need to know about all of them (ctrl+space is your friend!). The main class you need to know about is the *staged builder*, which serves as your entry point to performing operations using dynodao.

To make a query you new up the staged builder, specify the index to use and then the keys. Pass that object to `DynoDao#get` which will make the appropriate read operation for you. This results a class with a lazy stream which automatically performs the pagination for you, keeping only one page in memory at a time.

```java
DynoDao dynoDao = new DynoDao(amazonDynamoDb);
Stream<MySchema> queryResult = dynoDao.get(new MySchemaStagedDynamoBuilder()
        .usingIndexName()
        .withHashKey("hashKey")
        .withRangeKey("rangeKey"));
```
