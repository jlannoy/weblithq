package io.weblith.freemarker.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import freemarker.template.Template;
import io.weblith.core.form.Form;
import io.weblith.core.results.AbstractResult;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

public class HtmlResult extends AbstractResult<HtmlResult> {

    private final Optional<Template> template;

    private final Optional<String> templateDirectory, templateName, templateSuffix;

    private final Map<String, Object> templateParameters;

    public HtmlResult(String templateName) {
        this(null, templateName, null);
    }

    public HtmlResult(String templateDirectory, String templateName) {
        this(templateDirectory, templateName, null);
    }

    public HtmlResult(String templateDirectory, String templateName, String templateSuffix) {
        super(HtmlResult.class, MediaType.TEXT_HTML, Status.OK);
        this.template = Optional.empty();
        this.templateDirectory = Optional.ofNullable(templateDirectory);
        this.templateName = Optional.ofNullable(templateName);
        this.templateSuffix = Optional.ofNullable(templateSuffix);
        this.templateParameters = new HashMap<String, Object>();
    }

    public HtmlResult(Template template) {
        super(HtmlResult.class, MediaType.TEXT_HTML, Status.OK);
        this.template = Optional.ofNullable(template);
        this.templateDirectory = Optional.empty();
        this.templateName = Optional.empty();
        this.templateSuffix = Optional.empty();
        this.templateParameters = new HashMap<String, Object>();
    }

    public Optional<Template> getTemplate() {
        return template;
    }

    public Optional<String> getTemplateDirectory() {
        return templateDirectory;
    }

    public Optional<String> getTemplateName() {
        return templateName;
    }

    public Optional<String> getTemplateSuffix() {
        return templateSuffix;
    }

    public Map<String, Object> getTemplateParameters() {
        return templateParameters;
    }

    public HtmlResult render(String key, Object value) {
        this.templateParameters.put(key, value);
        return this;
    }

    public HtmlResult render(Form<?> form) {
        this.templateParameters.put(form.getFormName(), form);
        this.templateParameters.put(form.getValueName(), form.getValue());
        return this;
    }

}
