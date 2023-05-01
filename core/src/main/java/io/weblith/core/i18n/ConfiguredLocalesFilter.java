package io.weblith.core.i18n;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.quarkus.logging.Log;
import io.quarkus.runtime.LocalesBuildTimeConfig;
import io.weblith.core.config.WeblithConfig;
import io.weblith.core.request.RequestContext;
import io.weblith.core.scopes.CookieBuilder;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.NewCookie;

@Priority(Priorities.AUTHENTICATION - 10)
public class ConfiguredLocalesFilter implements LocaleHandler, ContainerRequestFilter { //, ContainerResponseFilter {

    protected final static String LANG_SESSION_KEY = "_lang";

    protected final String switchLanguageParam;

    protected final RequestContext context;

    protected final LocalesBuildTimeConfig localesConfig;

    protected final Map<String, Locale> byLanguageLocales;

    @Inject
    public ConfiguredLocalesFilter(RequestContext context, WeblithConfig weblithConfig, LocalesBuildTimeConfig localesConfig) {
        super();
        this.context = context;

        this.switchLanguageParam = weblithConfig.switchLanguageParam;
        this.localesConfig = localesConfig;
        this.byLanguageLocales = this.localesConfig.locales.stream()
                .collect(Collectors.toUnmodifiableMap(Locale::getLanguage, Function.identity(), (v1, v2) -> v1));

        Log.debugv("Language map initialized with : {0}", this.byLanguageLocales);
    }

    /**
     * Once a language proposal exists, make sure a corresponding locale exists in the application configuration. It
     * will not be possible to use any locale that have not be defined.
     */
    @Override
    public Locale validate(String language) {
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
     * Filter the incoming request. Try to find any language proposal for the current request:<br>
     * <ol>
     * <li>In the query parameters (if allowed via configuration)</li>
     * <li>In the request cookies (if previously set)</li>
     * <li>In the Accept-Language header of the current request</li>
     * <li>Else, fallback on the default (meaning first configured) locale</li>
     * </ol>
     * Set the result in the session scope.
     */
    @Override
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

    /**
     * Filter the outgoing response.
     */
    // @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        context.lookup(NewCookie.class).ifPresent(nc -> CookieBuilder.save(responseContext, nc));
    }

    @Override
    public Set<Locale> getApplicationLocales() {
        return this.localesConfig.locales;
    }

    @Override
    public Locale current() {
        return context.lookup(Locale.class).orElse(this.localesConfig.defaultLocale);
    }

}
