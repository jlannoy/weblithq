package io.weblith.core.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.test.junit.QuarkusTest;
import io.weblith.core.config.WeblithConfig;
import io.weblith.core.request.RequestContext;

@QuarkusTest
public class ResourceBundleMessagesTest {

    LocaleHandler localeHandler;

    RequestContext context;

    ResourceBundleMessages messages;

    WeblithConfig weblithConfig;

    @BeforeEach
    public void init() {
        this.weblithConfig = new WeblithConfig();
        this.weblithConfig.messagesPath = "i18n/messages";

        this.context = Mockito.mock(RequestContext.class);
        this.localeHandler = Mockito.mock(ConfiguredLocalesFilter.class);

        when(localeHandler.getApplicationLocales())
                .thenReturn(Set.of(new Locale("en"), new Locale("nl"), new Locale("fr", "FR"), new Locale("pl", "PL")));

        messages = new ResourceBundleMessages(localeHandler, weblithConfig);
    }

    @Test
    public void testGetMessage() {
        when(localeHandler.current()).thenReturn(new Locale("en"));
        assertTrue(messages.get("lang").isPresent());
        assertEquals("english", messages.get("lang").get());

        when(localeHandler.current()).thenReturn(new Locale("nl"));
        assertTrue(messages.get("lang").isPresent());
        assertEquals("dutch", messages.get("lang").get());

        when(localeHandler.current()).thenReturn(new Locale("fr", "FR"));
        assertTrue(messages.get("lang").isPresent());
        assertEquals("français", messages.get("lang").get());

        when(localeHandler.current()).thenReturn(new Locale("pl", "PL"));
        assertTrue(messages.get("lang").isPresent());
        assertEquals("default", messages.get("lang").get());
    }

    @Test
    public void testGetOverriddenReferenceMessage() {
        when(localeHandler.current()).thenReturn(new Locale("en"));
        assertEquals("Overridden", messages.get("common.overridden").get());

        when(localeHandler.current()).thenReturn(new Locale("nl"));
        assertEquals("Verwisseld", messages.get("common.overridden").get());

        when(localeHandler.current()).thenReturn(new Locale("fr", "FR"));
        assertEquals("Remplaçé", messages.get("common.overridden").get());
    }

    @Test
    public void testGetUndefinedMessage() {
        when(localeHandler.current()).thenReturn(new Locale("en"));
        assertEquals(Optional.empty(), messages.get("common.not_defined"));

        when(localeHandler.current()).thenReturn(new Locale("nl"));
        assertEquals(Optional.empty(), messages.get("common.not_defined"));

        when(localeHandler.current()).thenReturn(new Locale("fr", "FR"));
        assertEquals(Optional.empty(), messages.get("common.not_defined"));
    }

    @Test
    public void testGetMessageWithDefault() {
        when(localeHandler.current()).thenReturn(new Locale("en"));
        assertEquals("Overridden", messages.getWithDefault("common.overridden", "Default One"));
        assertEquals("Default one", messages.getWithDefault("common.not_defined", "Default one"));

        when(localeHandler.current()).thenReturn(new Locale("nl"));
        assertEquals("Verwisseld", messages.getWithDefault("common.overridden", "Default One"));
        assertEquals("Default One", messages.getWithDefault("common.not_defined", "Default One"));

        when(localeHandler.current()).thenReturn(new Locale("fr", "FR"));
        assertEquals("Remplaçé", messages.getWithDefault("common.overridden", "Default One"));
        assertEquals("Default One", messages.getWithDefault("common.not_defined", "Default One"));
    }

    @Test
    public void testGetMessageWithFormat() {
        when(localeHandler.current()).thenReturn(new Locale("en"));
        assertEquals("Message with MyValue", messages.get("common.format", "MyValue").get());

        when(localeHandler.current()).thenReturn(new Locale("nl"));
        assertEquals("Boodschap met MyValue", messages.get("common.format", "MyValue").get());

        when(localeHandler.current()).thenReturn(new Locale("fr", "FR"));
        assertEquals("Message avec MyValue", messages.get("common.format", "MyValue").get());
    }

    @Test
    public void testGetMessageWithDateFormat() {
        Date date = Date.from(LocalDateTime.of(1970, 1, 1, 1, 1).toInstant(ZoneOffset.UTC));

        when(localeHandler.current()).thenReturn(new Locale("en"));
        assertEquals("Dated Jan 1, 1970", messages.get("common.format_date", date).get());

        when(localeHandler.current()).thenReturn(new Locale("nl"));
        assertEquals("Voor datum 1 jan. 1970", messages.get("common.format_date", date).get());

        when(localeHandler.current()).thenReturn(new Locale("fr", "FR"));
        assertEquals("En date du 1 janv. 1970", messages.get("common.format_date", date).get());
    }

    @Test
    public void testGetMessageWithSpecialCharacters() {
        when(localeHandler.current()).thenReturn(new Locale("fr", "FR"));
        assertEquals("{} {'}", messages.get("format.curly_brace").get());
        assertEquals("Aujourd'hui", messages.get("format.quote").get());
        assertEquals("le \"texte\" cité", messages.get("format.double_quote").get());
    }

}
