package test.controllers;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.core.router.annotations.Post;

// tag::class[]
@Controller("/Controller")
public class MySecondController {

    @Get("/page")
    public Response myPage() { // <1>
        return Response.ok().build();
    }

    @Get("/page2/{key}")
    public Response myPage2(@PathParam("key") String id) { // <2>
        return Response.ok(id).build();
    }

    @Post("/action")
    public Response myAction() { // <3>
        return Response.ok().build();
    }

}
// end::class[]