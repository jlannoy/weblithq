package io.weblith.freemarker.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class TemplateConfig {

    /**
     * Templates directory.
     */
    @ConfigItem(defaultValue = "templates")
    public String directory;

    /**
     * Templates suffix
     */
    @ConfigItem(defaultValue = ".ftlh")
    public String suffix;

}