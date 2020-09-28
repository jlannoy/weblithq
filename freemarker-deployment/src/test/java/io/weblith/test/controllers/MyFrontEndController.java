package io.weblith.test.controllers;

import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import io.weblith.core.logging.NotLogged;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.freemarker.response.HtmlResult;

@Controller("/Front")
@NotLogged
public class MyFrontEndController {

    @Get
    public HtmlResult hello(@QueryParam("name") String name) {
        System.out.println("Test");
        return new HtmlResult("hello").render("name", name);
    }

    @Get
    public Response exception() {
        throw new WebApplicationException("My error message");
    }

}
