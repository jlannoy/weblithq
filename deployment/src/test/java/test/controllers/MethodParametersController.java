package test.controllers;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import jakarta.ws.rs.DefaultValue;

@Controller("/params")
public class MethodParametersController {

    @Get
    public String pathParam(@PathParam String param) {
        return "get " + param;
    }

    @Get("/named/pathParam/{param}")
    public String namedPathParam(@PathParam("param") String param) {
        return "get " + param;
    }

    @Get
    public String queryParam(@QueryParam String value) {
        return "get " + value;
    }

    @Get("/named/queryParam")
    public String namedQueryParam(@QueryParam("value") String value) {
        return "get " + value;
    }

    @Get
    public String optionalQueryParam(@QueryParam @DefaultValue("default-value") String value) {
        return "get " + value;
    }

    @Get("/named/optionalQueryParam")
    public String namedOptionalQueryParam(@QueryParam("value") @DefaultValue("default-value") String value) {
        return "get " + value;
    }

    // TODO Other annotations
}
