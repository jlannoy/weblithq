package test.controllers;

import io.weblith.core.request.RequestContext;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import jakarta.inject.Inject;

@Controller("/Ctx")
public class RequestContextAwareController {

    @Inject
    RequestContext context;

    @Get
    public String locale() {
        return context.locale().current().toString();
    }

    @Get
    public String authenticityToken() {
        return context.session().getAuthenticityToken();
    }

    @Get
    public String controllerName() {
        return context.controller().getSimpleName();
    }

}