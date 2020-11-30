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
     * Will indicate that a result implementation is able to render content as a response.
     */
    public static interface RenderResponse {
        void write(final OutputStream outputStream) throws Exception;
    }

    /**
     * Will indicate that a result implementation is able to configure a response.
     */
    public static interface ConfigureResponse {
        void configure(RequestContext requestContext, ContainerResponseContext responseContext);
    }

}