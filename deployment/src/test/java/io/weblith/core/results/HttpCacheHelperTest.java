package io.weblith.core.results;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.quarkus.test.junit.QuarkusTest;
import io.weblith.core.config.HttpCacheConfig;
import io.weblith.core.config.WeblithConfig;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

@QuarkusTest
public class HttpCacheHelperTest {

    @Mock
    TextResult result;

    @Mock
    ContainerRequestContext request;

    WeblithConfig weblithConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        weblithConfig = new WeblithConfig();
        weblithConfig.httpCache = new HttpCacheConfig();
    }

    @Test
    public void testIsModified() {

        HttpCacheHelper httpCacheHelper = new HttpCacheHelper(weblithConfig);

        // no header = modified
        assertTrue(httpCacheHelper.isModified(request, 0L));

        // no header value = modified
        when(request.getHeaderString(HttpHeaders.IF_MODIFIED_SINCE)).thenReturn(null);
        assertTrue(httpCacheHelper.isModified(request, 0L));

        // older timestamp = modified
        when(request.getHeaderString(HttpHeaders.IF_MODIFIED_SINCE)).thenReturn("Thu, 01 Jan 1970 00:00:00 GMT");
        assertTrue(httpCacheHelper.isModified(request, 1000L));

        // same timestamp = not modified
        when(request.getHeaderString(HttpHeaders.IF_MODIFIED_SINCE)).thenReturn("Thu, 01 Jan 1970 00:00:00 GMT");
        assertFalse(httpCacheHelper.isModified(request, 0L));

        // newer timestamp = not modified
        when(request.getHeaderString(HttpHeaders.IF_MODIFIED_SINCE)).thenReturn("Fri, 01 Jan 1970 00:01:00 GMT");
        assertFalse(httpCacheHelper.isModified(request, 0L));

        // invalid timestamp = modified
        when(request.getHeaderString(HttpHeaders.IF_MODIFIED_SINCE)).thenReturn("INVALID_TIMESTAMP");
        assertTrue(httpCacheHelper.isModified(request, 0L));

    }

    @Test
    public void testCacheControl() {

        weblithConfig.httpCache.enabled = false;
        HttpCacheHelper httpCacheHelper = new HttpCacheHelper(weblithConfig);
        httpCacheHelper.setCacheControl(result, 0L);
        verify(result).addHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store");

        reset(result);
        weblithConfig.httpCache.enabled = true;
        weblithConfig.httpCache.cacheControl = Duration.ofSeconds(1234L);
        httpCacheHelper = new HttpCacheHelper(weblithConfig);
        httpCacheHelper.setCacheControl(result, 0L);
        verify(result).addHeader(HttpHeaders.CACHE_CONTROL, "max-age=1234");

        // Cache control = 0 = no-cache
        reset(result);
        weblithConfig.httpCache.enabled = true;
        weblithConfig.httpCache.cacheControl = Duration.ofSeconds(0L);
        httpCacheHelper = new HttpCacheHelper(weblithConfig);
        httpCacheHelper.setCacheControl(result, 0L);
        verify(result).addHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store");

    }

}
