package io.weblith.webtest.domains.user;

import java.util.Date;

import freemarker.template.Template;
import io.quarkiverse.freemarker.TemplatePath;
import io.weblith.core.request.RequestContext;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.freemarker.response.HtmlResult;
import jakarta.inject.Inject;

@Controller
public class UserController {

    @Inject
    RequestContext context;

    @Inject
            @TemplatePath("UserController/list.ftlh")
    Template list;

    @Get
    public HtmlResult list() {

        String lastVisit = context.session().get("last-visit");
        context.session().put("last-visit", new Date().toString());

        return new HtmlResult(list).render("Users", User.listAll()).render("lastVisit", lastVisit);
    }

}
