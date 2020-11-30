package io.weblith.core.config;

import java.time.Duration;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.weblith.core.results.StreamResult;

@ConfigGroup
public class HttpCacheConfig {

    /**
     * Enable the HTTP Cache management for all {@link StreamResult}.
     */
    @ConfigItem(defaultValue = "true")
    public boolean enabled;
    
    /**
     * Default Cache-Control value.
     */
    @ConfigItem(defaultValue = "P1D")
    public Duration cacheControl;

}