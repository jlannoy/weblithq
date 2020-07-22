package io.weblith.freemarker.template;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import freemarker.template.Configuration;
import io.weblith.core.request.RequestContext;
import io.weblith.freemarker.config.FreemarkerConfig;
import io.weblith.freemarker.response.HtmlResult;

@ApplicationScoped
public class TemplateResolver {

    @Inject
    FreemarkerConfig freemarkerConfig;

    @Inject
    Configuration freemarker;

    @Inject
    RequestContext context;

    public freemarker.template.Template resolve(HtmlResult result) throws IOException {
        if (result.getTemplatePath().isPresent()) {
            return freemarker.getTemplate(result.getTemplatePath().get());
        } else {
            return freemarker.getTemplate(buildTemplateLocation(result));
        }
    }

    public freemarker.template.Template resolve(String path) throws IOException {
        return freemarker.getTemplate(buildTemplateLocation(path));
    }

    public freemarker.template.Template resolve(String directory, String name) throws IOException {
        return freemarker.getTemplate(buildTemplateLocation(directory, name));
    }

    protected String buildTemplateLocation(HtmlResult result) {
        return buildTemplateLocation(
                result.getTemplateDirectory().orElse(context.controller().getSimpleName()),
                result.getTemplateName().orElseThrow(),
                result.getTemplateSuffix().orElse(freemarkerConfig.template.suffix));
    }

    protected String buildTemplateLocation(String path) {
        return String.format("%s/%s", freemarkerConfig.template.directory, path);
    }

    protected String buildTemplateLocation(String directory, String name) {
        return buildTemplateLocation(directory, name, freemarkerConfig.template.suffix);
    }

    protected String buildTemplateLocation(String directory, String name, String suffix) {
        return String.format("%s/%s/%s%s", freemarkerConfig.template.directory, directory, name, suffix);
    }

}
