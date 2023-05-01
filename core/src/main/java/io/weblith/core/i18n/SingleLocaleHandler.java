package io.weblith.core.i18n;

import java.util.Locale;
import java.util.Set;

import io.quarkus.runtime.LocalesBuildTimeConfig;
import jakarta.inject.Inject;

public class SingleLocaleHandler implements LocaleHandler {

    private final Locale locale;

    @Inject
    public SingleLocaleHandler(LocalesBuildTimeConfig localesConfig) {
        // Default config set to default locale
        this.locale = localesConfig.locales.iterator().next();
    }

    @Override
    public Locale validate(String language) {
        return this.locale.getLanguage().equals(language)
                || this.locale.toString().equals(language)
                ? this.locale : null;
    }

    public Set<Locale> getApplicationLocales() {
        return Set.of(this.locale);
    }

    public Locale current() {
        return this.locale;
    }

}
