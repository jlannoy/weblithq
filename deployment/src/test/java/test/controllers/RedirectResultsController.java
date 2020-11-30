package test.controllers;

import io.weblith.core.results.Redirect;
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

}
