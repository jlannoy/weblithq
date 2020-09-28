package io.weblith.test.controllers;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;

import io.weblith.core.logging.NotLogged;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.freemarker.response.HtmlResult;
import io.weblith.freemarker.template.FreemarkerTemplate;
import io.weblith.freemarker.template.TemplatePath;

@Controller("/Front")
@NotLogged
public class InjectedTemplateController {

    @Inject
    FreemarkerTemplate bonjour;

    @TemplatePath("directory/ola.ftlh")
    FreemarkerTemplate pathTemplate;

    @Get
    public HtmlResult bonjour(@QueryParam("name") String name) {
        return bonjour.render("name", name);
    }

    @Get
    public HtmlResult ola(@QueryParam("name") String name) {
        return pathTemplate.render("name", name);
    }

}
