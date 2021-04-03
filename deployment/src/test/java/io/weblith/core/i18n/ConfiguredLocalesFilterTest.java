package io.weblith.core.i18n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.*;

import io.quarkus.runtime.LocalesBuildTimeConfig;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.weblith.core.config.WeblithConfig;
import io.weblith.core.request.RequestContext;
import io.weblith.core.scopes.SessionScope;
import io.weblith.core.scopes.WeblithScopesProducer;
import org.jboss.resteasy.spi.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

@QuarkusTest
public class ConfiguredLocalesFilterTest {

    final static String switchParameter = "my_lang";

    @Inject
    WeblithScopesProducer producer;

    @InjectMock
    RequestContext requestContext;

    @InjectMock
    SessionScope sessionScope;

    HttpRequest httpRequest;

    HttpHeaders httpHeaders;

    ContainerRequestContext containerRequestContext;

    ConfiguredLocalesFilter localeHandler;

    WeblithConfig weblithConfig;

    LocalesBuildTimeConfig localesConfig;

    @BeforeEach
    public void init() {
        this.weblithConfig = new WeblithConfig();
        this.weblithConfig.switchLanguageParam = switchParameter;

        this.localesConfig = new LocalesBuildTimeConfig();
        this.localesConfig.locales = new LinkedHashSet<>(List.of(new Locale("en"), new Locale("de"), new Locale("fr", "FR")));
        this.localesConfig.defaultLocale = localesConfig.locales.iterator().next();

        this.localeHandler = new ConfiguredLocalesFilter(requestContext, weblithConfig, localesConfig);

        this.httpRequest = Mockito.mock(HttpRequest.class);
        this.httpHeaders = Mockito.mock(HttpHeaders.class);
        this.containerRequestContext = Mockito.mock(ContainerRequestContext.class);

        when(requestContext.request()).thenReturn(httpRequest);
        when(requestContext.session()).thenReturn(sessionScope);
        when(httpRequest.getHttpHeaders()).thenReturn(httpHeaders);
    }

    @Test
    public void testGetApplicationLocales() {
        assertThat(localeHandler.getApplicationLocales(), contains(new Locale("en"), new Locale("de"), new Locale("fr", "FR")));

        // Reinit to see changes
        localesConfig.locales = new LinkedHashSet<>(List.of(new Locale("en", "US"), new Locale("en", "UK"), new Locale("en", "CA")));
        this.localeHandler = new ConfiguredLocalesFilter(requestContext, weblithConfig, localesConfig);
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
    public void testIdentifyCurrentLocaleFromParameters() throws IOException {
        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.of("en"));
        localeHandler.filter(containerRequestContext);
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));

        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.of("fr"));
        localeHandler.filter(containerRequestContext);
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("fr", "FR")));

        // fallback on default locale
        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.of("nl"));
        localeHandler.filter(containerRequestContext);
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));
    }

    @Test
    public void testIdentifyCurrentLocaleFromSession() throws IOException {
        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.empty());

        when(sessionScope.lookup(ConfiguredLocalesFilter.LANG_SESSION_KEY)).thenReturn(Optional.of("en"));
        localeHandler.filter(containerRequestContext);
        // assertThat(localeHandler.identifyCurrentLocale(),is(new Locale("en")));

        when(sessionScope.lookup(ConfiguredLocalesFilter.LANG_SESSION_KEY)).thenReturn(Optional.of("fr"));
        localeHandler.filter(containerRequestContext);
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("fr", "FR")));

        when(sessionScope.lookup(ConfiguredLocalesFilter.LANG_SESSION_KEY)).thenReturn(Optional.of("fr-FR"));
        localeHandler.filter(containerRequestContext);
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("fr", "FR")));

        // fallback on default locale
        when(sessionScope.lookup(ConfiguredLocalesFilter.LANG_SESSION_KEY)).thenReturn(Optional.of("nl"));
        localeHandler.filter(containerRequestContext);
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));
    }

    @Test
    public void testIdentifyCurrentLocaleFromHeaderParameter() throws IOException {
        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.empty());
        when(sessionScope.lookup(ConfiguredLocalesFilter.LANG_SESSION_KEY)).thenReturn(Optional.empty());

        when(httpHeaders.getAcceptableLanguages()).thenReturn(List.of(new Locale("en")));
        localeHandler.filter(containerRequestContext);
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));

        when(httpHeaders.getAcceptableLanguages()).thenReturn(List.of(new Locale("fr")));
        localeHandler.filter(containerRequestContext);
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("fr", "FR")));

        when(httpHeaders.getAcceptableLanguages()).thenReturn(List.of(new Locale("fr-FR")));
        localeHandler.filter(containerRequestContext);
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("fr", "FR")));

        when(httpHeaders.getAcceptableLanguages()).thenReturn(List.of(new Locale("nl"), new Locale("fr")));
        localeHandler.filter(containerRequestContext);
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("fr", "FR")));

        // fallback on default locale
        when(httpHeaders.getAcceptableLanguages()).thenReturn(List.of(new Locale("nl")));
        localeHandler.filter(containerRequestContext);
        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));
    }

    @Test
    public void testIdentifyCurrentLocaleAsDefaultOne() throws IOException {
        when(requestContext.getParameterValue(switchParameter)).thenReturn(Optional.empty());
        when(sessionScope.lookup(ConfiguredLocalesFilter.LANG_SESSION_KEY)).thenReturn(Optional.empty());
        // when(requestContext.getRequest().getAcceptLanguage()).thenReturn(Optional.empty());

        assertThat(localeHandler.identifyCurrentLocale(), is(new Locale("en")));
    }

}
