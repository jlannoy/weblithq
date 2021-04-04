package io.weblith.core.i18n;

import io.weblith.core.results.AbstractResult;

import java.util.Locale;
import java.util.Set;

public interface LocaleHandler {

    Locale validate(String language);

    Set<Locale> getApplicationLocales();

    Locale current();

}