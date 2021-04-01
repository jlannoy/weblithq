package io.weblith.core.i18n;

import io.weblith.core.results.AbstractResult;

import java.util.Locale;
import java.util.Set;

public interface LocaleHandler {

    Set<Locale> getApplicationLocales();

    Locale current();

}