package io.weblith.core.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.weblith.core.router.annotations.Get;
import io.weblith.core.router.annotations.Post;

@ConfigGroup
public class RequestLoggingConfig {

    /**
     * Enable a log for each {@link Post} or {@link Get} method.
     */
    @ConfigItem(defaultValue = "true")
    public boolean enabled;
    
    /**
     * Log request details.
     */
    @ConfigItem(defaultValue = "true")
    public boolean requestDetails;

    /**
     * Log request parameters.
     */
    @ConfigItem(defaultValue = "true")
    public boolean requestParameters;

    /**
     * Log user information.
     */
    @ConfigItem(defaultValue = "true")
    public boolean requestUser;

}