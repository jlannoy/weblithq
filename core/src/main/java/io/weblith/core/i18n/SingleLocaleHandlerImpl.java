package io.weblith.core.i18n;

import java.util.Locale;
import java.util.Set;

public class SingleLocaleHandlerImpl implements LocaleHandler {

    private final Locale locale;
    
    public SingleLocaleHandlerImpl(Locale locale) {
        this.locale = locale;
    }

    public Set<Locale> getApplicationLocales() {
        return Set.of(this.locale);
    }

    public Locale current() {
        return this.locale;
    }

}
