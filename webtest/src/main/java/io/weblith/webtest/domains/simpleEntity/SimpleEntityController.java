package io.weblith.webtest.domains.simpleEntity;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.quarkus.security.identity.SecurityIdentity;
import io.weblith.core.form.Form;
import io.weblith.core.request.RequestContext;
import io.weblith.core.results.Redirect;
import io.weblith.core.results.Result;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.core.router.annotations.Post;
import io.weblith.freemarker.response.HtmlResult;
import io.weblith.freemarker.template.FreemarkerTemplate;
import io.weblith.webtest.DummyDataGenerator;
import io.weblith.webtest.domains.simpleEntity.SimpleEntitiesForm.NestedForm;
import io.weblith.webtest.domains.simpleEntity.SimpleEntity.Type;

@Controller
public class SimpleEntityController {

    private final static Logger LOGGER = Logger.getLogger(DummyDataGenerator.class);

    @Inject
    SecurityIdentity identity;

    @Inject
    FreemarkerTemplate list, view, edit, multiple;

    @Inject
    RequestContext context;

    @Get
    public HtmlResult list() {
        return list.render("SimpleEntities", SimpleEntity.listAll());
    }

    @Get
    public HtmlResult view(@PathParam Long id) {
        return view.render("SimpleEntity", SimpleEntity.findById(id));
    }

    @Get
    public HtmlResult name(@PathParam String name) {
        return view.render("SimpleEntity", SimpleEntity.findByName(name));
    }

    @Get
    public Result redirect() {
        context.flash().success("Redirect with success !");
        return new Redirect("/SimpleEntity/list");
    }

    protected HtmlResult displayForm(Form<SimpleEntity> form) {
        return edit.render("Types", Type.values()).render(form);
    }

    @Get
    public HtmlResult create() {
        return displayForm(Form.of(new SimpleEntity()));
    }

    @Get
    public HtmlResult edit(@PathParam Long id) {
        return displayForm(Form.of(SimpleEntity.findById(id)));
    }

    @Transactional
    @Post
    public Result save(Form<SimpleEntity> form) {
        SimpleEntity dto = form.getValue();

        if (!form.validate()) {
            return displayForm(form);
        }

        SimpleEntity entity = dto.id != null ? SimpleEntity.findById(dto.id) : new SimpleEntity();

        entity.name = dto.name;
        entity.quantity = dto.quantity;
        entity.type = dto.type;
        entity.date = dto.date;

        entity.persist();
        return new Redirect("/SimpleEntity/list").withSuccess("entity.saved");
    }

    @Get
    public HtmlResult createMultiple() {
        return multiple.render(Form.of(new SimpleEntitiesForm()));
    }

    @Transactional
    @Post
    public Result saveMultiple(Form<SimpleEntitiesForm> form) {

        if (form.hasViolations()) {
            multiple.render(form);
        }

        SimpleEntitiesForm dto = form.getValue();

        for (NestedForm nested : dto.entities) {
            LOGGER.info(nested.name + "/" + nested.type + "/" + nested.date);
        }

        return new Redirect("/SimpleEntity/list").withSuccess(dto.entities + " saved");
    }

}
