package io.weblith.core.i18n;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;
import java.util.Map;
import java.util.Set;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;

import org.jboss.logging.Logger;
import org.jboss.resteasy.core.ResteasyContext;

import io.quarkus.runtime.LocalesBuildTimeConfig;
import io.weblith.core.config.WeblithConfiguration;
import io.weblith.core.request.RequestContext;
import io.weblith.core.scopes.CookieBuilder;

@ApplicationScoped
@Priority(Priorities.HEADER_DECORATOR + 10)
public class LocaleHandlerImpl implements LocaleHandler, ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LocaleHandlerImpl.class);

    private final static int ONE_YEAR = 60 * 60 * 24 * 365;

    private final String switchLanguageParam;

    private final String cookieName;

    private final RequestContext context;

    private final CookieBuilder cookieBuilder;

    private final LocalesBuildTimeConfig localesConfig;

    private final Map<String, Locale> byLanguageLocales;

    @Inject
    public LocaleHandlerImpl(RequestContext context, WeblithConfiguration weblithConfig, LocalesBuildTimeConfig localesConfig,
            CookieBuilder cookieBuilder) {
        super();
        this.context = context;
        this.cookieBuilder = cookieBuilder;

        this.switchLanguageParam = weblithConfig.switchLanguageParam;
        this.cookieName = weblithConfig.cookies.languageName;
        this.localesConfig = localesConfig;

        Map<String, Locale> map = new HashMap<String, Locale>();
        this.localesConfig.locales.forEach(l -> {
            map.put(l.getLanguage(), l);
            map.put(l.toString(), l);
        });
        this.byLanguageLocales = Collections.unmodifiableMap(map);

        LOGGER.debugv("Language map initialized with : {0}", this.byLanguageLocales);
    }

    /**
     * Once a language proposal exists, make sure a corresponding locale exists in the application configuration. It
     * will not be possible to use any locale that have not be defined.
     */
    protected Locale validate(String language) {
        return byLanguageLocales.get(language);
    }

    /**
     * Tries to find any language proposal for the current request:<br>
     * <ol>
     * <li>In the query parameters (if allowed via configuration)</li>
     * <li>In the request cookies (if previously set)</li>
     * <li>In the Accept-Language header of the current request</li>
     * <lI>Else, fallback on the default (meaning first configured) locale</li>
     * </ol>
     */
    public void filter(ContainerRequestContext requestContext) throws IOException {

        boolean setUpCookie = true;
        Locale currentLocale = null;

        // Step 1 : Check if change requested
        currentLocale = context.getParameterValue(switchLanguageParam).map(v -> validate(v)).orElse(null);

        // Step 2 : Check if a cookie value has already been set in past
        if (currentLocale == null) {
            currentLocale = context.getCookieValue(cookieName).map(v -> validate(v)).orElse(null);
            if (currentLocale != null) {
                setUpCookie = false;
            }
        }

        // Step 3 : Check if a header value exist
        if (currentLocale == null) {
            HttpHeaders httpHeaders = ResteasyContext.getContextData(HttpHeaders.class);
            if (httpHeaders != null) {
                List<String> acceptLanguageList = httpHeaders.getRequestHeader(HttpHeaders.ACCEPT_LANGUAGE);
                if (acceptLanguageList != null && !acceptLanguageList.isEmpty()) {
                    currentLocale = Locale.filter(LanguageRange.parse(acceptLanguageList.get(0)), localesConfig.locales)
                            .stream().findFirst().orElse(null);
                }
            }
        }

        // Step 4 : Fallback on default locale
        if (currentLocale == null) {
            currentLocale = this.localesConfig.defaultLocale;
        }

        if (setUpCookie) {
            context.seed(NewCookie.class, cookieBuilder.build(cookieName, currentLocale.toString(), ONE_YEAR));
        }

        context.seed(Locale.class, currentLocale);

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
