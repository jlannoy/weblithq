package io.weblith.fomantic;

import freemarker.template.Configuration;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class FomanticUIFreemarkerConfigurator {

    public final static String MACRO_PATH = "/macro/";

    public void configureFreemarker(@Observes StartupEvent startup, Configuration freemarker) {

        freemarker.addAutoImport("f", MACRO_PATH + "form.ftlh");
        freemarker.addAutoImport("layout", MACRO_PATH + "layout.ftlh");
        freemarker.addAutoImport("list", MACRO_PATH + "list.ftlh");
        freemarker.addAutoImport("menu", MACRO_PATH + "menu.ftlh");
        freemarker.addAutoImport("t", MACRO_PATH + "table.ftlh");
        freemarker.addAutoImport("tabs", MACRO_PATH + "tabs.ftlh");

    }

}
