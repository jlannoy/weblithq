package io.weblith.freemarker.response.exceptions;

import io.weblith.freemarker.response.HtmlResult;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
@Priority(Priorities.USER)
public class AnyExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        System.out.println("****************");

        HtmlResult htmlResult = new HtmlResult("layout", "error", "ftlh").render("message", exception.getMessage());
        htmlResult.status(Response.Status.INTERNAL_SERVER_ERROR);
        return Response.serverError().entity(htmlResult).build();
        // return Response.serverError().build();
    }

}