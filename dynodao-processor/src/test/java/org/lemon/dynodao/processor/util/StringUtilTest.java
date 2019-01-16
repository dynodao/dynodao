package org.lemon.dynodao.processor.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.xml.internal.ws.util.StringUtils;
import org.junit.Test;

public class StringUtilTest {

    @Test
    public void capitalize_upperCaseLetter_returnsSameString() {
        assertThat(StringUtils.capitalize("String")).isEqualTo("String");
    }

    @Test
    public void capitalize_lowerCaseLetter_returnsSameString() {
        assertThat(StringUtils.capitalize("string")).isEqualTo("String");
    }

}