package org.lemon.dynodao.test;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.Data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Data
public final class Item implements Comparable<Item> {

    public static final Comparator<Item> COMPARATOR = Comparator.nullsLast(Comparator.comparing(Item::getString)
            .thenComparing(Item::getInteger));
    public static final Comparator<Item> REVERSE_COMPARATOR = COMPARATOR.reversed();

    private final String string;
    private final int integer;

    @Override
    public int compareTo(Item o) {
        return COMPARATOR.compare(this, o);
    }

    public static Item item(String string, int integer) {
        return new Item(string, integer);
    }

    public static Item[] items(String string, int integerStart, int integerEndExclusive) {
        return IntStream.range(integerStart, integerEndExclusive)
                .mapToObj(integer -> item(string, integer))
                .toArray(Item[]::new);
    }

    public Map<String, AttributeValue> serialize() {
        Map<String, AttributeValue> map = new LinkedHashMap<>();
        map.put("string", new AttributeValue(getString()));
        map.put("integer", new AttributeValue().withN(String.valueOf(getInteger())));
        return map;
    }

    public static Item deserialize(Map<String, AttributeValue> item) {
        return item(item.get("string").getS(), Integer.parseInt(item.get("integer").getN()));
    }

    public static Item[] concat(Item[]... items) {
        return Arrays.stream(items).flatMap(Arrays::stream).toArray(Item[]::new);
    }

}
