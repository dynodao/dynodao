package org.lemon.dynodao.processor.util;

import javax.lang.model.element.Element;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

    /**
     * Returns the simple name of the element with the first letter capitalized.
     * @param element the element
     * @return the simple name with the first letter capitalized
     */
    public static String capitalize(Element element) {
        return capitalize(element.getSimpleName().toString());
    }

    /**
     * Returns string with the first letter capitalized.
     * @param str the string
     * @return the string with the first letter capitalized
     */
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
