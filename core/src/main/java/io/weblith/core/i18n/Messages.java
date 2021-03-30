package io.weblith.core.i18n;

import java.util.Optional;

/**
 * Get a translated string. The language is determined by the current context defined locale. Translated strings can use
 * the MessageFormat.<br>
 * http://docs.oracle.com/javase/6/docs/api/java/text/MessageFormat.html
 */
public interface Messages {

    Optional<String> get(String key, Object... parameter);

    String getWithDefault(String key, String defaultMessage, Object... params);

}
