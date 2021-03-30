package io.weblith.core.results;

import java.io.OutputStream;

import javax.ws.rs.container.ContainerResponseContext;

import io.weblith.core.request.RequestContext;

/**
 * This is a marker only interface for representing a Weblith result ; extends {@link AbstractResult} if you need to
 * provide your own implementation.
 */
public interface Result {

    /**
     * Indicate that a result implementation is able to render content as a response.
     */
    interface RenderResponse {
        void write(final OutputStream outputStream) throws Exception;
    }

    /**
     * Indicate that a result implementation is able to configure a response.
     */
    interface ConfigureResponse {
        void configure(RequestContext requestContext, ContainerResponseContext responseContext);
    }

    /**
     * Indicate that a result implementation is able to set up its caching policy according to a last modified timestamp.
     */
    interface AutomaticCachingPolicy {
        long getLastModified();
        boolean isHttpCacheEnabled();
    }
}