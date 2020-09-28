package io.weblith.freemarker.template;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class FreemarkerTemplateProducer {

    private static final Logger LOGGER = Logger.getLogger(FreemarkerTemplateProducer.class);

    @Inject
    TemplateResolver templateResolver;

    @Produces
    FreemarkerTemplate getDefaultFreemarkerTemplate(InjectionPoint injectionPoint) {

        String name = null;
        if (injectionPoint.getMember() instanceof Field) {
            // For "@Inject Template items" use "items"
            name = injectionPoint.getMember().getName();
        } else {
            AnnotatedParameter<?> parameter = (AnnotatedParameter<?>) injectionPoint.getAnnotated();
            if (parameter.getJavaParameter().isNamePresent()) {
                name = parameter.getJavaParameter().getName();
            } else {
                name = injectionPoint.getMember().getName();
                LOGGER.warnf("Parameter name not present - using the method name as the template name instead %s", name);
            }
        }

        String directory = injectionPoint.getBean().getBeanClass().getSimpleName();

        try {
            // Fully load the template once
            return new FreemarkerTemplate(templateResolver.resolve(directory, name).getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    @Produces
    @TemplatePath("ignored")
    FreemarkerTemplate getTemplate(InjectionPoint injectionPoint) {

        // For templates injected with a @TemplatePath qualifier
        TemplatePath templatePath = null;
        for (Annotation qualifier : injectionPoint.getQualifiers()) {
            if (qualifier.annotationType().equals(TemplatePath.class)) {
                templatePath = (TemplatePath) qualifier;
                break;
            }
        }
        if (templatePath == null || templatePath.value().isEmpty()) {
            throw new IllegalStateException("No template resource path specified");
        }

        try {
            return new FreemarkerTemplate(templateResolver.resolve(templatePath.value()).getName());
        } catch (IOException e) {
            LOGGER.error(e);
            throw new IllegalStateException(e);
        }
    }

}
