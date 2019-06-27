package com.github.dynodao.processor.util;

import com.github.dynodao.processor.test.AbstractUnitTest;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.junit.jupiter.api.Test;

import javax.annotation.Generated;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class DynoDaoUtilTest extends AbstractUnitTest {

    @Test
    void generatedAnnotation_onlyUseCase_returnsAnnotation() {
        AnnotationSpec generated = DynoDaoUtil.generatedAnnotation();

        // assertions field by field, as the date contains the current time
        assertThat(generated.type).isEqualTo(TypeName.get(Generated.class));

        Map<String, List<CodeBlock>> members = generated.members;
        assertThat(members).containsOnlyKeys("value", "date", "comments");

        assertThat(members).contains(entry("value", singletonList(CodeBlock.of("\"com.github.dynodao.processor\""))));
        assertThat(members).contains(entry("comments", singletonList(CodeBlock.of("\"https://github.com/dynodao/dynodao\""))));

        List<CodeBlock> date = members.get("date");
        assertThat(date)
                .hasSize(1)
                .element(0)
                .satisfies(codeBlock -> {
                    String value = codeBlock.toString();
                    assertThat(value)
                            .startsWith("\"")
                            .endsWith("\"")
                            .hasSize(2 + ZonedDateTime.now().toString().length());
                    ZonedDateTime time = ZonedDateTime.parse(value.replaceAll("\"", ""));
                    assertThat(time).isBetween(ZonedDateTime.now().minusMinutes(1), ZonedDateTime.now());
                });
    }

}
