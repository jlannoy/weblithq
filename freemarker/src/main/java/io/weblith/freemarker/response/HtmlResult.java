package io.weblith.freemarker.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import io.weblith.core.form.Form;
import io.weblith.core.results.Result;
import io.weblith.freemarker.template.FreemarkerTemplate;

public class HtmlResult extends Result {

    private final Optional<String> templatePath, templateDirectory, templateName, templateSuffix;

    private final Map<String, Object> templateParameters;

    public HtmlResult(String templateName) {
        this(null, null, templateName, null);
    }

    public HtmlResult(String templateDirectory, String templateName) {
        this(null, templateDirectory, templateName, null);
    }

    public HtmlResult(String templateDirectory, String templateName, String templateSuffix) {
        this(null, templateDirectory, templateName, null);
    }

    private HtmlResult(String templatePath, String templateDirectory, String templateName, String templateSuffix) {
        super(MediaType.TEXT_HTML, Status.OK);
        this.templatePath = Optional.ofNullable(templatePath);
        this.templateDirectory = Optional.ofNullable(templateDirectory);
        this.templateName = Optional.ofNullable(templateName);
        this.templateSuffix = Optional.ofNullable(templateSuffix);
        this.templateParameters = new HashMap<String, Object>();
    }

    public HtmlResult(FreemarkerTemplate template) {
        this(template.getTemplatePath(), null, null, null);
    }

    public Optional<String> getTemplatePath() {
        return templatePath;
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
