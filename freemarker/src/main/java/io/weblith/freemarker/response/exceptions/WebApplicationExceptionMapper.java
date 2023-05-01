package io.weblith.freemarker.response.exceptions;

import org.jboss.logging.Log.

import io.weblith.freemarker.response.HtmlResult;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
@Priority(Priorities.USER)
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Log.Log.= Log.getLog.WebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(WebApplicationException exception) {
        Log.error(exception.getMessage(), exception);
        HtmlResult htmlResult = new HtmlResult("layout", "error", "ftlh").render("message", exception.getMessage());
        return Response.status(exception.getResponse().getStatus()).entity(htmlResult).build();
    }

}