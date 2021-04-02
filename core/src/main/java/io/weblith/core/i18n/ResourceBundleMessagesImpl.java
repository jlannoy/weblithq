package io.weblith.core.i18n;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;

import io.weblith.core.config.WeblithConfig;
import org.jboss.logging.Logger;

public class ResourceBundleMessagesImpl implements Messages {

    protected static final Logger LOGGER = Logger.getLogger(ResourceBundleMessagesImpl.class);

    protected final Map<Locale, ResourceBundle> resourceBundles;

    protected final LocaleHandler localeHandler;

    public ResourceBundleMessagesImpl(LocaleHandler localeHandler, WeblithConfig weblithConfig) {
        this.localeHandler = localeHandler;
        this.resourceBundles = loadResourceBundles(localeHandler, weblithConfig.messagesPath);

        LOGGER.debugv("{0} ResourceBundles loaded", this.resourceBundles.size());
    }

    private Map<Locale, ResourceBundle> loadResourceBundles(LocaleHandler localeHandler, String path) {
        Map<Locale, ResourceBundle> bundles = new HashMap<Locale, ResourceBundle>();
        localeHandler.getApplicationLocales().forEach(l -> {
            bundles.put(l, ResourceBundle.getBundle(path, l, Thread.currentThread().getContextClassLoader()));
        });
        return Collections.unmodifiableMap(bundles);
    }

    @Override
    public Optional<String> get(String key, Object... params) {
        ResourceBundle bundle = resourceBundles.get(localeHandler.current());
        if (bundle != null && bundle.containsKey(key)) {
            return Optional.of(new MessageFormat(bundle.getString(key), localeHandler.current()).format(params));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String getWithDefault(String key, String defaultMessage, Object... params) {
        return get(key, params).orElse(new MessageFormat(defaultMessage, localeHandler.current()).format(params));
    }
}