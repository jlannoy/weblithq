package io.weblith.core.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.weblith.core.scopes.CookieBuilder;
import io.weblith.core.scopes.SessionScope;

@ConfigGroup
public class FlashConfig {

    /**
     * Cookie name for holding {@link SessionScope} data.
     */
    @ConfigItem(defaultValue = "__flash__")
    public String cookieName;

    /**
     * Default domain to apply to cookies build via {@link CookieBuilder}.
     */
    @ConfigItem
    public Optional<String> cookieDomain;

    /**
     * Default path to apply to cookies build via {@link CookieBuilder}.
     */
    @ConfigItem
    public Optional<String> cookiePath;

    /**
     * Secure cookies configuration.
     */
    @ConfigItem(defaultValue = "false")
    public boolean cookieSecure;

    /**
     * HTTS-Only cookies configuration.
     */
    @ConfigItem(defaultValue = "false")
    public boolean cookieHttpsOnly;

}