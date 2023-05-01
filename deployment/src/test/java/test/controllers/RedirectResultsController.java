package test.controllers;

import io.weblith.core.results.Redirect;
import io.weblith.core.results.Result;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.core.router.annotations.Post;
import jakarta.ws.rs.core.Response.Status;

@Controller
public class RedirectResultsController {

    // tag::examples[]
    @Get
    public Result redirectToHome() {
        return new Redirect("/");
    }
    // end::examples[]

    @Get
    public Result simpleEntities() {
        return new Redirect("/SimpleEntity/list").status(Status.NOT_MODIFIED);
    }

    // tag::with[]
    @Post
    public Result withSuccess() {
        // do my stuff
        return new Redirect("/").withSuccess("you.win"); // <1>
    }

    @Post
    public Result withWarning() {
        // do my stuff
        return new Redirect("/").withWarning("you.lose"); // <2>
    }

    @Post
    public Result withError() {
        // do my stuff
        return new Redirect("/").withError("This is an error"); // <3>
    }
    // end::with[]
}
