package io.weblith.freemarker.template;

import freemarker.template.Configuration;
import io.weblith.core.request.RequestContext;
import io.weblith.freemarker.config.FreemarkerConfig;
import io.weblith.freemarker.response.HtmlResult;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class TemplateResolver {

    private static final Logger LOGGER = Logger.getLogger(TemplateResolver.class);

    @Inject
    FreemarkerConfig freemarkerConfig;

    @Inject
    Configuration freemarker;

    @Inject
    RequestContext context;

    public freemarker.template.Template resolve(HtmlResult result) throws IOException {
        if (result.getTemplatePath().isPresent()) {
            return loadTemplate(result.getTemplatePath().get());
        } else {
            return loadTemplate(buildTemplateLocation(result));
        }
    }

    public freemarker.template.Template resolve(String path) throws IOException {
        return loadTemplate(buildTemplateLocation(path));
    }

    public freemarker.template.Template resolve(String directory, String name) throws IOException {
        return loadTemplate(buildTemplateLocation(directory, name, freemarkerConfig.template.suffix));
    }

    protected freemarker.template.Template loadTemplate(String templatePath) throws IOException {
        LOGGER.debugv("Loading template at {0}", templatePath);
        freemarker.template.Template template = freemarker.getTemplate(templatePath);
        LOGGER.debugv("Loaded template {0}", template.getName());
        return template;
    }

    protected String buildTemplateLocation(HtmlResult result) {
        return buildTemplateLocation(
                result.getTemplateDirectory().orElseGet(() -> context.controller().getSimpleName()),
                result.getTemplateName().orElseThrow(),
                result.getTemplateSuffix().orElseGet(() -> freemarkerConfig.template.suffix));
    }

    protected String buildTemplateLocation(String path) {
        return String.format("/%s/%s", freemarkerConfig.template.directory, path);
    }

    protected String buildTemplateLocation(String directory, String name, String suffix) {
        return String.format("/%s/%s/%s%s", freemarkerConfig.template.directory, directory, name, suffix.startsWith(".") ? suffix : "." + suffix);
    }

}
