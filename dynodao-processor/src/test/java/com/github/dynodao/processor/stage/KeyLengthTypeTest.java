package com.github.dynodao.processor.stage;

import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.github.dynodao.processor.schema.attribute.NumberDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.StringDynamoAttribute;
import com.github.dynodao.processor.schema.index.DynamoIndex;
import com.github.dynodao.processor.schema.index.IndexType;
import com.github.dynodao.processor.test.AbstractUnitTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class KeyLengthTypeTest extends AbstractUnitTest {

    private static final DynamoAttribute HASH_KEY = StringDynamoAttribute.builder().build();
    private static final DynamoAttribute RANGE_KEY = NumberDynamoAttribute.builder().build();

    @Test
    void lengthOf_hash_returnsHash() {
        DynamoIndex index = index().rangeKey(Optional.empty()).build();
        assertThat(KeyLengthType.lengthOf(index)).isEqualTo(KeyLengthType.HASH);
    }

    @Test
    void lengthOf_range_returnsRange() {
        assertThat(KeyLengthType.lengthOf(index().build())).isEqualTo(KeyLengthType.RANGE);
    }

    @Test
    void getKeyAttributes_none_returnsEmptyList() {
        assertThat(KeyLengthType.NONE.getKeyAttributes(null)).isEmpty();
    }

    @Test
    void getKeyAttributes_hash_returnsHashKey() {
        assertThat(KeyLengthType.HASH.getKeyAttributes(index().build())).containsExactly(HASH_KEY);
    }

    @Test
    void getKeyAttributes_range_returnsHashAndRangeKey() {
        assertThat(KeyLengthType.RANGE.getKeyAttributes(index().build())).containsExactly(HASH_KEY, RANGE_KEY);
    }

    private DynamoIndex.DynamoIndexBuilder index() {
        return DynamoIndex.builder()
                .name("index-name")
                .indexType(IndexType.TABLE)
                .hashKey(HASH_KEY)
                .rangeKey(Optional.of(RANGE_KEY));
    }

}
