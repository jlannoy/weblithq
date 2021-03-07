package test.controllers;

import javax.ws.rs.core.Response.Status;

import io.weblith.core.results.Redirect;
import io.weblith.core.results.Result;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;

@Controller
public class RedirectResultsController {

    // tag::examples[]
    @Get
    public Redirect redirectToHome() {
        return new Redirect("/");
    }
    // end::examples[]

    @Get
    public Result simpleEntities() {
        return new Redirect("/SimpleEntity/list").status(Status.NOT_MODIFIED);
    }

    @Get
    public Result withSuccess() {
        return new Redirect("/").withSuccess("you.win");
    }

    @Get
    public Result withWarning() {
        return new Redirect("/").withWarning("you.lose");
    }

    @Get
    public Result withError() {
        return new Redirect("/").withError("This is an error");
    }
}
