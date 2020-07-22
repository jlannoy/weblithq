package io.weblith.core.config;

import java.time.Duration;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

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
     * Specific configuration only applicable to the session one.
     */
    public CookieConfig cookie;

}