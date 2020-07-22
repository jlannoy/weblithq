package io.weblith.freemarker.template;

import io.weblith.core.form.Form;
import io.weblith.freemarker.response.HtmlResult;

public class FreemarkerTemplate {

    private final String templatePath;

    public FreemarkerTemplate(String templatePath) {
        super();
        this.templatePath = templatePath;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public HtmlResult render() {
        return new HtmlResult(this);
    }

    public HtmlResult render(Form<?> form) {
        return new HtmlResult(this).render(form);
    }

    public HtmlResult render(String key, Object value) {
        return new HtmlResult(this).render(key, value);
    }

}
