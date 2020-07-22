package io.weblith.core.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "weblith", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class WeblithConfiguration {

    /**
     * Parameter that can be passed to any page to switch language. Set to empty String to disable.
     */
    @ConfigItem(defaultValue = "lang")
    public String switchLanguageParam;
    
    /**
     * Enable the use of MessageBundles for translations.
     */
    @ConfigItem(defaultValue = "false")
    public String i18nEnabled;

    public CookiesConfig cookies;

    public HttpCacheConfig httpCache;
    
    public RequestLoggingConfig requestLogs;

}
