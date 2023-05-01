package io.weblith.core.i18n;

import java.util.Locale;
import java.util.Set;

public interface LocaleHandler {

    Locale validate(String language);

    Set<Locale> getApplicationLocales();

    Locale current();

}