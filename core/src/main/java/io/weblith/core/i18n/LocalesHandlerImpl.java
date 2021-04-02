package io.weblith.core.i18n;

import io.quarkus.runtime.LocalesBuildTimeConfig;
import io.weblith.core.config.WeblithConfig;
import io.weblith.core.request.RequestContext;
import io.weblith.core.scopes.CookieBuilder;
import org.jboss.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
@Priority(Priorities.HEADER_DECORATOR + 10)
public class LocalesHandlerImpl implements LocaleHandler, ContainerRequestFilter, ContainerResponseFilter {

    protected static final Logger LOGGER = Logger.getLogger(LocalesHandlerImpl.class);

    protected final static String LANG_SESSION_KEY = "_lang";

    protected final String switchLanguageParam;

    protected final RequestContext context;

    protected final LocalesBuildTimeConfig localesConfig;

    protected final Map<String, Locale> byLanguageLocales;

    @Inject
    public LocalesHandlerImpl(RequestContext context, WeblithConfig weblithConfig, LocalesBuildTimeConfig localesConfig) {
        super();
        this.context = context;

        this.switchLanguageParam = weblithConfig.switchLanguageParam;
        this.localesConfig = localesConfig;
        this.byLanguageLocales = this.localesConfig.locales.stream()
                .collect(Collectors.toUnmodifiableMap(Locale::getLanguage, Function.identity(), (v1, v2) -> v1));

        LOGGER.debugv("Language map initialized with : {0}", this.byLanguageLocales);
    }

    /**
     * Once a language proposal exists, make sure a corresponding locale exists in the application configuration. It
     * will not be possible to use any locale that have not be defined.
     */
    protected Locale validate(String language) {
        if (language != null) {
            Locale locale = Locale.forLanguageTag(language);
            if (this.byLanguageLocales.values().contains(locale)) {
                return locale;
            }
            if (this.byLanguageLocales.containsKey(locale.getLanguage())) {
                return byLanguageLocales.get(locale.getLanguage());
            }
        }
        return null;
    }

    /**
     * Tries to find any language proposal for the current request:<br>
     * <ol>
     * <li>In the query parameters (if allowed via configuration)</li>
     * <li>In the request cookies (if previously set)</li>
     * <li>In the Accept-Language header of the current request</li>
     * <li>Else, fallback on the default (meaning first configured) locale</li>
     * </ol>
     * Set the result in the session scope.
     */
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Locale locale = identifyCurrentLocale();
        context.seed(Locale.class, locale);
        context.session().put(LANG_SESSION_KEY, locale.toString());
    }

    protected Locale identifyCurrentLocale() {
        Locale currentLocale = null;

        // Step 1 : Check if an explicit change was requested
        currentLocale = context.getParameterValue(switchLanguageParam).map(this::validate).orElse(null);

        // Step 2 : Check if a cookie value - via the session scope - has already been set in past
        if (currentLocale == null) {
            currentLocale = context.session().lookup(LANG_SESSION_KEY).map(this::validate).orElse(null);
        }

        // Step 3 : Check if a header value exist
        if (currentLocale == null) {
            for (Locale acceptedLocale : context.request().getHttpHeaders().getAcceptableLanguages()) {
                if (this.localesConfig.locales.contains(acceptedLocale)) {
                    currentLocale = acceptedLocale;
                } else {
                    currentLocale = validate(acceptedLocale.getLanguage());
                }
                if (currentLocale != null) {
                    break;
                }
            }
        }

        // Step 4 : Fallback on the default application locale
        if (currentLocale == null) {
            currentLocale = this.localesConfig.defaultLocale;
        }

        return currentLocale;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        context.lookup(NewCookie.class).ifPresent(nc -> CookieBuilder.save(responseContext, nc));
    }

    public Set<Locale> getApplicationLocales() {
        return this.localesConfig.locales;
    }

    public Locale current() {
        return context.lookup(Locale.class).orElse(this.localesConfig.defaultLocale);
    }

}
