package test.controllers;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.core.router.annotations.Post;
import io.weblith.core.security.NotCsrfProtected;

// tag::class[]
@Controller
public class MyFirstController {

    @Get
    public Response myPage() { // <1>
        return Response.ok().build();
    }

    @Get
    public Response myPage2(@PathParam String id) { // <2>
        return Response.ok(id).build();
    }

    @Post
    @NotCsrfProtected
    public Response myAction() { // <3>
        return Response.ok().build();
    }

}
// end::class[]