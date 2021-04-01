package io.weblith.core.config;

import java.time.Duration;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.weblith.core.scopes.CookieBuilder;
import io.weblith.core.scopes.SessionScope;

@ConfigGroup
public class SessionConfig {

    /**
     * Session expiration.
     */
    @ConfigItem(defaultValue = "P1D")
    public Duration expire;

    /**
     * Session renewal time.
     */
    @ConfigItem(defaultValue = "PT1H")
    public Duration renewal;

    /**
     * Secret.
     */
    @ConfigItem(defaultValue = "secret")
    public String secret;

    /**
     * Cookie name for holding {@link SessionScope} data.
     */
    @ConfigItem(defaultValue = "__session__")
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
    @ConfigItem(defaultValue = "true")
    public boolean cookieSecure;

    /**
     * HTTS-Only cookies configuration.
     */
    @ConfigItem(defaultValue = "true")
    public boolean cookieHttpsOnly;
}