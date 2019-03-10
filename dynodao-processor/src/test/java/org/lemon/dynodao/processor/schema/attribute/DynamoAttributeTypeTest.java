package org.lemon.dynodao.processor.schema.attribute;

import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

class DynamoAttributeTypeTest extends AbstractUnitTest {

    @Test
    void getDataTypeName_onlyUseCase_returnsCorrectName() {
        Map<DynamoAttributeType, String> expectedNames = new EnumMap<>(DynamoAttributeType.class);
        expectedNames.put(DynamoAttributeType.BINARY, "B");
        expectedNames.put(DynamoAttributeType.BINARY_SET, "BS");
        expectedNames.put(DynamoAttributeType.BOOLEAN, "BOOL");
        expectedNames.put(DynamoAttributeType.LIST, "L");
        expectedNames.put(DynamoAttributeType.MAP, "M");
        expectedNames.put(DynamoAttributeType.NUMBER, "N");
        expectedNames.put(DynamoAttributeType.NUMBER_SET, "NS");
        expectedNames.put(DynamoAttributeType.NULL, "NULL");
        expectedNames.put(DynamoAttributeType.STRING, "S");
        expectedNames.put(DynamoAttributeType.STRING_SET, "SS");

        Map<DynamoAttributeType, String> dataTypeNames = Arrays.stream(DynamoAttributeType.values())
                .collect(toMap(Function.identity(), DynamoAttributeType::getDataTypeName));

        assertThat(dataTypeNames).isEqualTo(expectedNames);
    }

    @Test
    void isScalar_onlyUseCase_returnsCorrectValue() {
        Map<DynamoAttributeType, Boolean> expectedScalar = new EnumMap<>(DynamoAttributeType.class);
        Arrays.stream(DynamoAttributeType.values()).forEach(type -> expectedScalar.put(type, false));
        expectedScalar.put(DynamoAttributeType.BINARY, true);
        expectedScalar.put(DynamoAttributeType.BOOLEAN, true);
        expectedScalar.put(DynamoAttributeType.NUMBER, true);
        expectedScalar.put(DynamoAttributeType.NULL, true);
        expectedScalar.put(DynamoAttributeType.STRING, true);

        Map<DynamoAttributeType, Boolean> scalar = Arrays.stream(DynamoAttributeType.values())
                .collect(toMap(Function.identity(), DynamoAttributeType::isScalar));

        assertThat(scalar).isEqualTo(expectedScalar);
    }

    @Test
    void isViableKey_onlyUseCase_returnsCorrectValue() {
        Map<DynamoAttributeType, Boolean> expectedViableKey = new EnumMap<>(DynamoAttributeType.class);
        Arrays.stream(DynamoAttributeType.values()).forEach(type -> expectedViableKey.put(type, false));
        expectedViableKey.put(DynamoAttributeType.BINARY, true);
        expectedViableKey.put(DynamoAttributeType.NUMBER, true);
        expectedViableKey.put(DynamoAttributeType.STRING, true);

        Map<DynamoAttributeType, Boolean> viableKey = Arrays.stream(DynamoAttributeType.values())
                .collect(toMap(Function.identity(), DynamoAttributeType::isViableKey));

        assertThat(viableKey).isEqualTo(expectedViableKey);
    }

}
