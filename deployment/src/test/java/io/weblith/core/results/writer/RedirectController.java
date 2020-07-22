package io.weblith.core.results.writer;

import javax.inject.Inject;

import io.weblith.core.logging.NotLogged;
import io.weblith.core.request.RequestContext;
import io.weblith.core.results.Redirect;
import io.weblith.core.results.Result;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;

@NotLogged
@Controller
public class RedirectController {

    @Inject
    RequestContext context;

    @Get
    public Result root() {
        return new Redirect("/");
    }

    @Get
    public Result simpleEntities() {
        return new Redirect("/SimpleEntity/list");
    }

    @Get
    public Result withSuccess() {
        return new Redirect("/").withSuccess("you.win");
    }
    
    @Get
    public Result withWarning() {
        return new Redirect("/").withWarning("you.win");
    }
    
    @Get
    public Result withError() {
        return new Redirect("/").withError("This is an error");
    }

}
