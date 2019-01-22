package org.lemon.dynodao.processor.util;

import org.junit.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamUtilTest extends AbstractUnitTest {

    @Test
    public void concat_noArgs_emptyStream() {
        assertThat(StreamUtil.concat()).isEmpty();
    }

    @Test
    public void concat_objectsOnly_streamOfArgs() {
        Object[] args = { 1, null, new Object() };
        assertThat(StreamUtil.concat(args)).containsExactly(args);
    }

    @Test
    public void concat_containsStream_flattensStream() {
        Object[] args = { Stream.of(1, 2, 3), Stream.of(null, "str") };
        assertThat(StreamUtil.concat(args)).containsExactly(1, 2, 3, null, "str");
    }

    @Test
    public void concat_containsObjectArray_flattensArray() {
        Object[][] args = { { "1", "2" }, { "3", "4" } };
        assertThat(StreamUtil.concat((Object[]) args)).containsExactly("1", "2", "3", "4");
    }

    @Test
    public void concat_containsPrimitiveArray_noFlatten() {
        int[][] args = { { 1, 2 } };
        assertThat(StreamUtil.concat((Object[]) args)).containsExactly((Object) (new int[] { 1, 2 }));
    }

    @Test
    public void concat_containsCollection_flattensCollection() {
        Object[] args = { Arrays.asList(1, 2), Arrays.asList(3, 4) };
        assertThat(StreamUtil.concat(args)).containsExactly(1, 2, 3, 4);
    }

}