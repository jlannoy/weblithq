package io.weblith.core.i18n;

import java.util.Locale;
import java.util.Set;

public interface LocaleHandler {

    Set<Locale> getApplicationLocales();

    Locale current();

}