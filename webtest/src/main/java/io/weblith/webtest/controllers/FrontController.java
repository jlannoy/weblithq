package io.weblith.webtest.controllers;

import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.freemarker.response.HtmlResult;

@Controller("")
public class FrontController {

    @Get("/")
    public HtmlResult home() {
        return new HtmlResult("Main", "home");
    }

}
