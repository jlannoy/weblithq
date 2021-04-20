package io.weblith.freemarker.response.exceptions;

import io.weblith.freemarker.response.HtmlResult;
import org.jboss.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
@Priority(Priorities.USER)
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOGGER = Logger.getLogger(WebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(WebApplicationException exception) {
        LOGGER.error(exception.getMessage(), exception);
        HtmlResult htmlResult = new HtmlResult("layout", "error", "ftlh").render("message", exception.getMessage());
        return Response.status(exception.getResponse().getStatus()).entity(htmlResult).build();
    }

}