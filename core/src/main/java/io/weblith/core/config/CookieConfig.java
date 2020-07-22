package io.weblith.core.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.weblith.core.scopes.CookieBuilder;

@ConfigGroup
public class CookieConfig {

    /**
     * Default domain to apply to cookies build via {@link CookieBuilder}.
     */
    @ConfigItem
    public Optional<String> domain;

    /**
     * Default prefix to apply to cookies build via {@link CookieBuilder}.
     */
    @ConfigItem
    public Optional<String> prefix;

    /**
     * Default path to apply to cookies build via {@link CookieBuilder}.
     */
    @ConfigItem
    public Optional<String> path;

    /**
     * Secure cookies configuration.
     */
    @ConfigItem(defaultValue = "false")
    public boolean secure;

    /**
     * HTTS-Only cookies configuration.
     */
    @ConfigItem(defaultValue = "false")
    public boolean httpsOnly;

}