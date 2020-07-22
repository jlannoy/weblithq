package io.weblith.webtest.controllers;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import io.weblith.core.logging.NotLogged;
import io.weblith.core.results.Redirect;
import io.weblith.core.results.Result;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.core.scopes.SessionScope;
import io.weblith.freemarker.template.FreemarkerTemplate;

@Controller("/auth")
public class AuthenticationController {

    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    String authCookieName;

    @Inject
    SessionScope session;

    @Inject
    RoutingContext routingContext;

    @Inject
    FreemarkerTemplate login;

    @Get
    public Result login() {
        return login.render();
    }

    @Get
    public Result error() {
        return login.render("error", "Authentication failed");
    }

    @Get
    @NotLogged
    public Result logout() {
        session.invalidate();
        routingContext.addCookie(Cookie.cookie(authCookieName, "").setPath("/").setMaxAge(0));
        return new Redirect("/RestrictedAccess/list");
    }

}
