package io.weblith.freemarker.response.exceptions;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.weblith.freemarker.response.HtmlResult;

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