package io.weblith.core.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.weblith.core.scopes.CookieBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ConfigGroup
public class TenantsConfig {

    /**
     * Default tenant identifier.
     */
    @ConfigItem(defaultValue = "public")
    public String defaultTenant;

    /**
     * Domain name part allowing to define subdomains. (Without the trailing .)
     */
    @ConfigItem
    public Optional<String> domain;

    /**
     * Subdomains to math to the given domain. The tenant identifier will be the subdomain name itself.
     */
    @ConfigItem
    public Optional<List<String>> subdomains;

    /**
     * Full domain names each related to their specific tenant identifiers.
     */
    @ConfigItem(name = "domain")
    public Map<String, String> domains;

}
