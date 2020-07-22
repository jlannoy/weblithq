package io.weblith.test.controllers;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import io.weblith.core.logging.NotLogged;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.freemarker.response.HtmlResult;
import io.weblith.freemarker.template.FreemarkerTemplate;
import io.weblith.freemarker.template.TemplatePath;

@Controller("/Front")
@NotLogged
public class MyFrontEndController {

    @Inject
    FreemarkerTemplate bonjour;

    @TemplatePath("MyFrontEndController/ola")
    FreemarkerTemplate pathTemplate;

    @Get
    public HtmlResult hello(@QueryParam("name") String name) {
        return new HtmlResult("hello").render("name", name);
    }

    @Get
    public HtmlResult bonjour(@QueryParam("name") String name) {
        return bonjour.render("name", name);
    }
    
    @Get
    public HtmlResult ola(@QueryParam("name") String name) {
        return pathTemplate.render("name", name);
    }

    @Get
    public Response exception() {
        throw new WebApplicationException("My error message");
    }

}
