package io.weblith.test.controllers;

import freemarker.template.Template;
import io.quarkiverse.freemarker.TemplatePath;
import io.weblith.core.logging.NotLogged;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.freemarker.response.HtmlResult;
import io.weblith.freemarker.template.FreemarkerTemplate;
import jakarta.inject.Inject;
import jakarta.ws.rs.QueryParam;

@Controller("/Front")
@NotLogged
public class InjectedTemplateController {

    @Inject
    FreemarkerTemplate bonjour;

    @Inject
    @TemplatePath("directory/ola.ftl")
    FreemarkerTemplate pathTemplate;

    @Inject
    @TemplatePath("MyFrontEndController/hello.ftlh")
    Template originalFreemarkerTemplate;

    @Get
    public HtmlResult bonjour(@QueryParam("name") String name) {
        return bonjour.render("name", name);
    }

    @Get
    public HtmlResult ola(@QueryParam("name") String name) {
        return pathTemplate.render("name", name);
    }

    @Get
    public HtmlResult hello(@QueryParam("name") String name) {
        return new HtmlResult(originalFreemarkerTemplate).render("name", name);
    }
}
