package io.weblith.core.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.weblith.core.scopes.CookieBuilder;

import java.util.Map;

@ConfigGroup
public class TenantsConfig {

    /**
     * Domains.
     */
    @ConfigItem
    public Map<String, String> domains;

    /**
     * Subdomains.
     */
    @ConfigItem
    public Map<String, String> subdomains;

}
