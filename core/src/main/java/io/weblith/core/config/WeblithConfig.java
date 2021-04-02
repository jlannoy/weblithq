package io.weblith.core.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.weblith.core.router.annotations.Post;
import io.weblith.core.security.AuthenticityTokenFilter;

@ConfigRoot(name = "weblith", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class WeblithConfig {

    /**
     * Parameter that can be passed to any page to switch language. Set to empty String to disable.
     */
    @ConfigItem(defaultValue = "lang")
    public String switchLanguageParam;

    /**
     * Path of the translated messages properties files.
     */
    @ConfigItem(defaultValue = "i18n/messages")
    public String messagesPath;

    /**
     * Enable the use of MessageBundles for translations.
     */
    @ConfigItem(defaultValue = "false")
    public String i18nEnabled;

    public SessionConfig session;

    public FlashConfig flash;

    public HttpCacheConfig httpCache;

    public RequestLoggingConfig requestLogs;

    /**
     * Enable a CSRF protection for each {@link Post} method, via an {@link AuthenticityTokenFilter}.
     */
    @ConfigItem(defaultValue = "true")
    public boolean csrfProtected;

}
