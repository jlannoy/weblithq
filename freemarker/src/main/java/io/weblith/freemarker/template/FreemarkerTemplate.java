package io.weblith.freemarker.template;

import freemarker.template.Template;
import io.weblith.core.form.Form;
import io.weblith.freemarker.response.HtmlResult;

public class FreemarkerTemplate {

    private final Template template;

    public FreemarkerTemplate(Template template) {
        super();
        this.template = template;
    }

    public HtmlResult render() {
        return new HtmlResult(template);
    }

    public HtmlResult render(Form<?> form) {
        return new HtmlResult(template).render(form);
    }

    public HtmlResult render(String key, Object value) {
        return new HtmlResult(template).render(key, value);
    }

}
