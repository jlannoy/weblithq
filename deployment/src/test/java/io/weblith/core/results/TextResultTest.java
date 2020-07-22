package io.weblith.core.results;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

import io.weblith.core.results.TextResult;

public class TextResultTest {

    @Test
    public void testObjectShouldBeRenderedAsString() throws IOException {

        Map<String, String> map = Maps.newLinkedHashMap();
        map.put("aaa", "bbb");
        map.put("111", "222");
        assertEquals("{aaa=bbb, 111=222}", new TextResult(map).content);

    }

}
