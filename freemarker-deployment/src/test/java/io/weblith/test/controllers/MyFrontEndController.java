package io.weblith.test.controllers;

import io.weblith.core.logging.NotLogged;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.freemarker.response.HtmlResult;

import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Controller("/Front")
@NotLogged
public class MyFrontEndController {

    @Get
    public HtmlResult hello(@QueryParam("name") String name) {
        System.out.println("Test");
        return new HtmlResult("hello").render("name", name);
    }

    @Get
    public HtmlResult bonjour(@QueryParam("name") String name) {
        return new HtmlResult("InjectedTemplateController", "bonjour").render("name", name);
    }

    @Get
    public HtmlResult ola(@QueryParam("name") String name) {
        return new HtmlResult("directory", "ola", "ftl").render("name", name);
    }

    @Get
    public Response exception() {
        throw new WebApplicationException("My error message");
    }

}
