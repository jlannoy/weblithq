package io.weblith.webtest;

import freemarker.template.Configuration;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class TemplatesConfigurator {

    public void configureFreemarker(@Observes StartupEvent startup, Configuration freemarkerConfiguration) {

        freemarkerConfiguration.addAutoImport("page", "webtest-layouts.ftlh");

    }

}
