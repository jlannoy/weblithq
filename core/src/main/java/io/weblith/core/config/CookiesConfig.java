package io.weblith.core.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.weblith.core.scopes.FlashScope;
import io.weblith.core.scopes.SessionScope;

@ConfigGroup
public class CookiesConfig {

    /**
     * Cookie name for holding current user language.
     */
    @ConfigItem(defaultValue = "__lang__")
    public String languageName;

    /**
     * Cookie name for holding {@link FlashScope} data.
     */
    @ConfigItem(defaultValue = "__flash__")
    public String flashName;

    /**
     * Cookie name for holding {@link SessionScope} data.
     */
    @ConfigItem(defaultValue = "__session__")
    public String sessionName;

    public CookieConfig config;

    public SessionConfig session;

}