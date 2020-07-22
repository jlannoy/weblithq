package test.controllers;

import javax.ws.rs.DefaultValue;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import io.weblith.core.logging.NotLogged;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.core.router.annotations.Post;
import io.weblith.core.security.NotCsrfProtected;

@NotLogged
@Controller("/weblith")
public class SimpleFullyValuedWeblithController {

    @Get("/get")
    public String get() {
        return "get weblith";
    }

    @Post("/post")
    @NotCsrfProtected
    public String post() {
        return "post weblith";
    }

    @Get("/pathParam/{param}")
    public String pathParam(@PathParam("param") String param) {
        return "get " + param;
    }
    
    @Get("/queryParam")
    public String queryParam(@QueryParam("value") String value) {
        return "get " + value;
    }
    
    @Get("/optionalQueryParam")
    public String optionalQueryParam(@QueryParam("value") @DefaultValue("default-value") String value) {
        return "get " + value;
    }

}
