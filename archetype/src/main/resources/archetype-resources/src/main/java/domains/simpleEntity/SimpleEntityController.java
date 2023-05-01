#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domains.simpleEntity;

import java.nio.charset.StandardCharsets;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Log.
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.quarkus.security.identity.SecurityIdentity;
import io.weblith.core.form.Form;
import io.weblith.core.request.RequestContext;
import io.weblith.core.results.Redirect;
import io.weblith.core.results.Result;
import io.weblith.core.results.TextResult;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.core.router.annotations.Post;
import io.weblith.freemarker.response.HtmlResult;
import io.weblith.freemarker.template.FreemarkerTemplate;
import ${package}.domains.simpleEntity.SimpleEntity.Type;

@Controller
public class SimpleEntityController {

    @Inject
    SecurityIdentity identity;

    @Inject
    FreemarkerTemplate list, view, edit;

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
        entity.quantity2 = dto.quantity2;
        entity.type = dto.type;
        entity.date = dto.date;
        
        entity.persist();
        return new Redirect("/SimpleEntity/list").withSuccess("entity.saved");
    }

}
