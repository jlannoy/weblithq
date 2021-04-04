package io.weblith.core.i18n;

import io.quarkus.runtime.LocalesBuildTimeConfig;
import io.weblith.core.results.AbstractResult;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import java.util.Locale;
import java.util.Set;

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
