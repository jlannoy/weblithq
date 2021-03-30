package io.weblith.core.i18n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.*;

import io.quarkus.runtime.LocalesBuildTimeConfig;
import io.weblith.core.config.CookiesConfig;
import io.weblith.core.config.WeblithConfig;
import io.weblith.core.request.RequestContext;
import io.weblith.core.scopes.CookieBuilder;
import org.jboss.resteasy.spi.HttpRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Maps;
import com.google.inject.Provider;

import io.undertow.server.handlers.Cookie;

@RunWith(MockitoJUnitRunner.class)
public class LocalesHandlerImplTest {

    final static String cookieName = "cookie_LANG";

    final static String switchParameter = "my_lang";

    @Mock
    Provider<Locale> currentLocaleProvider;

    @Mock
    RequestContext requestContext;

    @Mock
    HttpRequest httpRequest;

    @Mock
    CookieBuilder cookieBuilder;

    Map<String, Cookie> cookies = Maps.newHashMap();

    LocalesHandlerImpl localeHandler;

    WeblithConfig weblithConfig;

    LocalesBuildTimeConfig localesConfig;

    @Before
    public void init() {
        weblithConfig = new WeblithConfig();
        weblithConfig.switchLanguageParam = switchParameter;
        weblithConfig.cookies = new CookiesConfig();
        weblithConfig.cookies.languageName = cookieName;

        localesConfig = new LocalesBuildTimeConfig();
        localesConfig.locales = new LinkedHashSet<>(List.of(new Locale("en"), new Locale("de"), new Locale("fr", "FR")));
        localesConfig.defaultLocale = localesConfig.locales.iterator().next();

        this.cookies = Maps.newHashMap();

        when(requestContext.request()).thenReturn(httpRequest);

        this.localeHandler = new LocalesHandlerImpl(requestContext, weblithConfig, localesConfig, cookieBuilder);
    }

    @Test
    public void testGetApplicationLocales() {
        assertThat(localeHandler.getApplicationLocales(), contains(new Locale("en"), new Locale("de"), new Locale("fr", "FR")));

        // Reinit to see changes
        localesConfig.locales = new LinkedHashSet<>(List.of(new Locale("en", "US"), new Locale("en", "UK"), new Locale("en", "CA")));
        this.localeHandler = new LocalesHandlerImpl(requestContext, weblithConfig, localesConfig, cookieBuilder);
        assertThat(localeHandler.getApplicationLocales(), contains(new Locale("en", "US"), new Locale("en", "UK"), new Locale("en", "CA")));
        assertThat(localeHandler.byLanguageLocales, aMapWithSize(1));
        assertThat(localeHandler.byLanguageLocales.values(), contains(new Locale("en", "US")));
    }

    @Test
    public void testValidateLocales() {
        assertNull(localeHandler.validate(null));
        assertNull(localeHandler.validate("nl"));
        assertNull(localeHandler.validate("nl-FR"));

        assertThat(localeHandler.validate("en"), is(new Locale("en")));
        assertThat(localeHandler.validate("en-US"), is(new Locale("en")));
        assertThat(localeHandler.validate("en-CA"), is(new Locale("en")));
        assertThat(localeHandler.validate("en-UK"), is(new Locale("en")));

        assertThat(localeHandler.validate("de"), is(new Locale("de")));
        assertThat(localeHandler.validate("de-DE"), is(new Locale("de")));

        assertThat(localeHandler.validate("fr"), is(new Locale("fr", "FR")));
        assertThat(localeHandler.validate("fr-FR"), is(new Locale("fr", "FR")));
        // fr-FR is the first locale defined that will handle fr languages
        assertThat(localeHandler.validate("fr-BE"), is(new Locale("fr", "FR")));
        assertThat(localeHandler.validate("fr-FR;q=0.9"), is(new Locale("fr", "FR")));
    }

    @Test
    public void testIdentifyCurrentLocaleFromParameters() {
        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.of("en"));
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));
        assertThat(cookies, hasKey(cookieName));
        cookies.clear();

        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.of("fr"));
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("fr", "FR")));
        assertThat(cookies, hasKey(cookieName));
        cookies.clear();

        // fallback on default locale
        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.of("nl"));
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));
        assertThat(cookies, hasKey(cookieName));
    }

    @Test
    public void testIdentifyCurrentLocaleFromRequestCookies() {
        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.empty());

        when(requestContext.getCookieValue(cookieName)).thenReturn(Optional.of("en"));
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));
        assertThat(cookies, anEmptyMap());

        when(requestContext.getCookieValue(cookieName)).thenReturn(Optional.of("fr"));
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("fr", "FR")));
        assertThat(cookies, anEmptyMap());

        // fallback on default locale
        when(requestContext.getCookieValue(cookieName)).thenReturn(Optional.of("nl"));
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));
        assertThat(cookies, hasKey(cookieName));
    }

//    @Test
//    public void testIdentifyCurrentLocaleFromHeaderParameter() {
//        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.empty());
//        when(requestContext.getCookieValue(cookieName)).thenReturn(Optional.empty());
//
//        when(requestContext.getRequest().getAcceptLanguage()).thenReturn(Optional.of("en"));
//        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));
//        assertThat(cookies, hasKey(cookieName));
//        cookies.clear();
//
//        when(requestContext.getCookieValue(cookieName)).thenReturn(Optional.empty());
//        when(requestContext.getRequest().getAcceptLanguage()).thenReturn(Optional.of("fr-FR"));
//        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("fr", "FR")));
//        assertThat(cookies, hasKey(cookieName));
//        cookies.clear();
//
//        // fallback on default locale
//        when(requestContext.getCookieValue(cookieName)).thenReturn(Optional.empty());
//        when(requestContext.getRequest().getAcceptLanguage()).thenReturn(Optional.of("nl"));
//        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));
//        assertThat(cookies, hasKey(cookieName));
//    }

    @Test
    public void testIdentifyCurrentLocaleAsDefaultOne() {
        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.empty());
        when(requestContext.getCookieValue(cookieName)).thenReturn(Optional.empty());
        // when(requestContext.getRequest().getAcceptLanguage()).thenReturn(Optional.empty());

        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));
        assertThat(cookies, hasKey(cookieName));

        Cookie cookie = cookies.get(cookieName);
        assertThat(cookie.getName(), is(cookieName));
        assertThat(cookie.getValue(), is("en"));
    }

}
