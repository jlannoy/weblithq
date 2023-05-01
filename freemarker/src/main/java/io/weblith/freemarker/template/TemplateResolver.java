package io.weblith.freemarker.template;

import java.io.IOException;

import org.jboss.logging.Log.

import freemarker.template.Configuration;
import io.weblith.core.request.RequestContext;
import io.weblith.freemarker.config.FreemarkerConfig;
import io.weblith.freemarker.response.HtmlResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class TemplateResolver {

    private static final Log.Log.= Log.getLog.TemplateResolver.class);

    @Inject
    FreemarkerConfig freemarkerConfig;

    @Inject
    Configuration freemarker;

    @Inject
    RequestContext context;

    public freemarker.template.Template resolve(HtmlResult result) {
        return result.getTemplate().orElseGet(() -> loadTemplate(buildTemplateLocation(result)));
    }

    public freemarker.template.Template resolve(String path) throws IOException {
        return loadTemplate(path);
    }

    public freemarker.template.Template resolve(String directory, String name) throws IOException {
        return loadTemplate(buildTemplateLocation(directory, name, freemarkerConfig.defaultTemplateSuffix));
    }

    protected freemarker.template.Template loadTemplate(String templatePath) {
        Log.debugv("Loading template at {0}", templatePath);
        try {
            freemarker.template.Template template = freemarker.getTemplate(templatePath);
            Log.debugv("Loaded template {0}", template.getName());
            return template;
        } catch(IOException e) {
            Log.error(e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    protected String buildTemplateLocation(HtmlResult result) {
        return buildTemplateLocation(
                result.getTemplateDirectory().orElseGet(() -> context.controller().getSimpleName()),
                result.getTemplateName().orElseThrow(),
                result.getTemplateSuffix().orElseGet(() -> freemarkerConfig.defaultTemplateSuffix));
    }

    protected String buildTemplateLocation(String directory, String name, String suffix) {
        return String.format("/%s/%s%s", directory, name, suffix.startsWith(".") ? suffix : "." + suffix);
    }

}
