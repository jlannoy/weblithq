#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import freemarker.template.Configuration;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class TemplatesConfigurator {

    public void configureFreemarker(@Observes StartupEvent startup, Configuration freemarkerConfiguration) {

        freemarkerConfiguration.addAutoImport("page", "templates/app-layouts.ftlh");

    }

}
