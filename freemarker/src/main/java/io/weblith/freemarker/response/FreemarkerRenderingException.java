package io.weblith.freemarker.response;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class FreemarkerRenderingException extends WebApplicationException {

    public FreemarkerRenderingException() {
        super(Response.Status.INTERNAL_SERVER_ERROR);
    }

    public FreemarkerRenderingException(String message, Throwable cause) {
        super(message, cause, Response.Status.INTERNAL_SERVER_ERROR);
    }

    public FreemarkerRenderingException(String message) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR);
    }

    public FreemarkerRenderingException(Throwable cause) {
        super(cause, Response.Status.INTERNAL_SERVER_ERROR);
    }

}
