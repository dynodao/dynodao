package org.lemon.dynodao.processor.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;
import org.mockito.Answers;
import org.mockito.Mock;

import javax.lang.model.element.Element;

public class StringUtilTest extends AbstractUnitTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private Element elementMock;

    @Test
    public void capitalize_upperCaseLetter_returnsSameString() {
        assertThat(StringUtil.capitalize("String")).isEqualTo("String");
    }

    @Test
    public void capitalize_lowerCaseLetter_capitalizesFirstLetter() {
        assertThat(StringUtil.capitalize("string")).isEqualTo("String");
    }

    @Test
    public void capitalize_upperCaseSimpleNameElement_returnsSimpleNameAsString() {
        when(elementMock.getSimpleName().toString()).thenReturn("String");
        assertThat(StringUtil.capitalize(elementMock)).isEqualTo("String");
    }

    @Test
    public void capitalize_lowerCaseSimpleNameElement_capitalizesFirstLetterOfSimpleName() {
        when(elementMock.getSimpleName().toString()).thenReturn("string");
        assertThat(StringUtil.capitalize(elementMock)).isEqualTo("String");
    }

    @Test
    public void toClassCase_alreadyInClassCase_returnsSameString() {
        assertThat(StringUtil.toClassCase("ThisIsClassCase")).isEqualTo("ThisIsClassCase");
    }

    @Test
    public void toClassCase_allLowerCase_capitalizeFirstLetter() {
        assertThat(StringUtil.toClassCase("class")).isEqualTo("Class");
    }

    @Test
    public void toClassCase_containsNumericFirstCharacter_ignoreIllegalCharAndCapitalizeNext() {
        assertThat(StringUtil.toClassCase("1class")).isEqualTo("Class");
    }

    @Test
    public void toClassCase_containsNumbersAsSubsequentCharacters_onlyCapitalizeFirstLetter() {
        assertThat(StringUtil.toClassCase("c1l3a4s5s6")).isEqualTo("C1l3a4s5s6");
    }

    @Test
    public void toClassCase_containsIllegalChars_capitalizeLettersAfterIllegalChars() {
        assertThat(StringUtil.toClassCase("<table><One>")).isEqualTo("TableOne");
    }

    @Test
    public void toClassCase_typicalDynamoIndexName_returnsClassCase() {
        assertThat(StringUtil.toClassCase("hash-key-range-key-index")).isEqualTo("HashKeyRangeKeyIndex");
    }

    @Test
    public void repeat_zeroRepeats_returnsEmptyString() {
        assertThat(StringUtil.repeat(0, "toRepeat", "delimiter")).isEmpty();
    }

    @Test
    public void repeat_oneRepeat_returnsStringOnly() {
        assertThat(StringUtil.repeat(1, "string", "delimiter")).isEqualTo("string");
    }

    @Test
    public void repeat_multipleRepeats_returnsStringsSpacedByDelimiter() {
        assertThat(StringUtil.repeat(3, "string", ", ")).isEqualTo("string, string, string");
    }

}