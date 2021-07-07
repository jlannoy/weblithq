package io.weblith.freemarker;

import freemarker.cache.*;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import io.weblith.freemarker.config.FreemarkerConfig;
import no.api.freemarker.java8.Java8ObjectWrapper;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

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
