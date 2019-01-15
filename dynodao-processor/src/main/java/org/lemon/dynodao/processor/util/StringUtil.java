package org.lemon.dynodao.processor.util;

import static java.util.stream.Collectors.joining;

import java.util.stream.Stream;

import javax.lang.model.element.Element;

import lombok.experimental.UtilityClass;

/**
 * Utility methods for manipulating strings.
 */
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

    /**
     * Returns the string as a valid class name, removing spaces, dashes, etc. Any time there is an invalid
     * character, it is removed and the next letter is capitalized.
     * @param str the string to transform into a class name
     * @return <tt>str</tt> as a class name
     */
    public static String toClassCase(String str) {
        char[] s = str.toCharArray();
        StringBuilder builder = new StringBuilder();
        boolean titleCaseNext = true;
        for (int i = 0; i < s.length; ++i) {
            if (!isLegalIdentifierCharacter(s[i], i)) {
                titleCaseNext = true;
            } else if (titleCaseNext) {
                builder.append(Character.toTitleCase(s[i]));
                titleCaseNext = false;
            } else {
                builder.append(s[i]);
            }
        }
        return builder.toString();
    }

    private static boolean isLegalIdentifierCharacter(char ch, int index) {
        return Character.isJavaIdentifierStart(ch) || (index > 0 && Character.isJavaIdentifierPart(ch));
    }

    /**
     * Repeats the string <tt>n</tt> times, putting the <tt>delimiter</tt> between each repetition.
     * @param n the number of times to repeat
     * @param toRepeat the string to repeat
     * @param delimiter the delimiter to place between each string
     * @return
     */
    public static String repeat(int n, String toRepeat, String delimiter) {
        return Stream.iterate(0, i -> i + 1)
                .limit(n)
                .map(i -> toRepeat)
                .collect(joining(delimiter));
    }

}
