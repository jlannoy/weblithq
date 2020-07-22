package io.weblith.test.controllers;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;

import io.weblith.core.logging.NotLogged;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.freemarker.response.HtmlResult;
import io.weblith.freemarker.template.FreemarkerTemplate;

@Controller("/TemplateError")
@NotLogged
public class ErroneousTemplateController {

    @Inject
    FreemarkerTemplate erroneous;

    @Get
    public HtmlResult hello(@QueryParam("name") String name) {
        return erroneous.render("name", name);
    }
    
}
