package io.weblith.webtest.controllers;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.freemarker.response.HtmlResult;
import io.weblith.freemarker.template.FreemarkerTemplate;
import io.weblith.webtest.domains.user.UserRole;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

@Controller
public class RestrictedAccessController {

    @Inject
    SecurityIdentity identity;

    FreemarkerTemplate myTemplate;

    @Get
    public HtmlResult list() {
        return myTemplate.render("Identity", identity);
    }

    @Get
    @Authenticated
    public HtmlResult auth() {
        return list().render("state", "Authenticated page");
    }

    @Get
    @RolesAllowed(UserRole.ADMIN)
    public HtmlResult admin() {
        return list().render("state", "Restricted admin page");
    }

    @Get
    @RolesAllowed(UserRole.MANAGER)
    public HtmlResult manager() {
        return list().render("state", "Restricted manager page ; should be access by admin too");
    }

    @Get
    @RolesAllowed(UserRole.USER)
    public HtmlResult user() {
        return list().render("state", "Restricted user page ; should be access by admin and manager too");
    }

}
