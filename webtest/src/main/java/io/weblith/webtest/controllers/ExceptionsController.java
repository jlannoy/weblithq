package io.weblith.webtest.controllers;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import io.weblith.core.results.Result;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.freemarker.response.HtmlResult;
import io.weblith.freemarker.template.FreemarkerTemplate;

@Controller
public class ExceptionsController {

    @Inject
    FreemarkerTemplate list;

    @Get
    public HtmlResult list() {
        return list.render();
    }

    @Get
    public Result web404() {
        throw new WebApplicationException(404);
    }
    
    @Get
    public Result illegal() {
        throw new IllegalArgumentException("Invalid argument");
    }
}
