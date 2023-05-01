package io.weblith.freemarker;

import java.io.File;
import java.io.IOException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Log.

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MruCacheStorage;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;
import io.quarkus.runtime.configuration.ProfileManager;
import io.weblith.freemarker.config.FreemarkerConfig;
import jakarta.inject.Inject;
import no.api.freemarker.java8.Java8ObjectWrapper;

@Deprecated
// @Startup(Interceptor.Priority.PLATFORM_BEFORE)
// @Singleton
public class FreemarkerConfigurationProvider {

    private final static Log.Log.= Log.getLog.FreemarkerConfigurationProvider.class);

    @Inject
    FreemarkerConfig config;

    @ConfigProperty(name = "quarkus.http.root-path")
    String contextPath;

    @Inject
    FreemarkerComponentsProvider components;

    // Replaced by quarkiverse extension
//    @Produces
//    @DefaultBean
//    @ApplicationScoped
    public Configuration configureFreemarker() {
        Configuration freemarker = new Configuration(Configuration.VERSION_2_3_29);
        freemarker.setDefaultEncoding("UTF-8");
        freemarker.setOutputEncoding("UTF-8");
        freemarker.setLocalizedLookup(false);

        boolean devMode = ProfileManager.getActiveProfile().equals("dev");

        configureTemplateLoading(freemarker, devMode);

        // http://freemarker.sourceforge.net/docs/app_faq.html#faq_number_grouping
        freemarker.setNumberFormat("0.######");

        freemarker.setObjectWrapper(createBeansWrapperWithExposedFields());

        if (devMode) {
            freemarker.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            freemarker.setLogTemplateExceptions(true);
            freemarker.setWrapUncheckedExceptions(false);
        } else {
            freemarker.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            freemarker.setLogTemplateExceptions(false);
            freemarker.setWrapUncheckedExceptions(true);
        }
        freemarker.setFallbackOnNullLoopVariable(false);

        configureCommonComponents(freemarker);

        return freemarker;
    }

    private BeansWrapper createBeansWrapperWithExposedFields() {
        Java8ObjectWrapper wrapper = new Java8ObjectWrapper(Configuration.VERSION_2_3_29);
        wrapper.setExposeFields(true);
        return wrapper;
    }

    private boolean configureTemplateLoading(Configuration freemarker, boolean devMode) {
        // As we run from the target directory, we should be able to directly access 
        // the templates source directory when running in dev mode

        String srcDir = String.join(File.separator, System.getProperty("user.dir"), "..", "src", "main", "resources");

        if (devMode && new File(srcDir).exists()) {

            try {

                FileTemplateLoader srcLoader = new FileTemplateLoader(new File(srcDir));
                ClassTemplateLoader cpLoader = new ClassTemplateLoader(Thread.currentThread().getContextClassLoader(), "/");
                freemarker.setTemplateLoader(new MultiTemplateLoader(new TemplateLoader[] { srcLoader, cpLoader }));

            } catch (IOException e) {
                Log.error("Error loading Freemarker Template directory " + srcDir, e);
            }

            freemarker.setCacheStorage(new MruCacheStorage(0, Integer.MAX_VALUE));
            freemarker.setTemplateUpdateDelayMilliseconds(1000);

        } else {

            freemarker.setClassLoaderForTemplateLoading(Thread.currentThread().getContextClassLoader(), "/");

            // Hold 20 templates as strong references in production
            freemarker.setCacheStorage(new MruCacheStorage(20, Integer.MAX_VALUE));
            // never update the templates in production or while testing...
            freemarker.setTemplateUpdateDelayMilliseconds(Integer.MAX_VALUE);

        }
        return devMode;
    }

    private void configureCommonComponents(Configuration freemarker) {
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
