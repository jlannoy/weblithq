package io.weblith.freemarker;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@Dependent
public class FreemarkerConfigurator {

    @ConfigProperty(name = "quarkus.http.root-path")
    String contextPath;

    @Inject
    FreemarkerComponentsProvider components;

    public void configureFreemarkerAtStartup(@Observes StartupEvent event, Configuration freemarker) {
        try {

            freemarker.setSharedVariable("contextPath", this.contextPath.equals("/") ? "" : this.contextPath);

            freemarker.setSharedVariable("authenticityToken", components.authenticityToken());
            freemarker.setSharedVariable("authenticityHiddenField", components.authenticityHiddenField());

            freemarker.setSharedVariable("i18n", components.i18nMethod());
            freemarker.setSharedVariable("prettyTime", components.prettyTimeMethod());

        } catch (TemplateModelException e) {
            throw new IllegalStateException(e);
        }
    }

}
