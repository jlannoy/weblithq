package test.controllers;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import io.weblith.core.logging.NotLogged;
import io.weblith.core.results.Redirect;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.core.router.annotations.Post;
import io.weblith.core.security.NotCsrfProtected;

@NotLogged
@Controller
public class SimpleWeblithController {

    @Get
    public Response get() {
        return Response.ok().entity("get weblith").build();
    }

    @Post
    @NotCsrfProtected
    public String post() {
        return "post weblith";
    }

    @Get
    public String pathParam(@PathParam String param) {
        return "get " + param;
    }

    @Get
    public String queryParam(@QueryParam String value) {
        return "get " + value;
    }

    @Get
    public String optionalQueryParam(@QueryParam @DefaultValue("default-value") String value) {
        return "get " + value;
    }

    @Get
    public Redirect redirect() {
        return new Redirect("/");
    }
}
