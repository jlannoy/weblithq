package io.weblith.core.i18n;

import io.weblith.core.results.AbstractResult;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import java.util.Locale;
import java.util.Set;

@Priority(Priorities.HEADER_DECORATOR + 10)
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
