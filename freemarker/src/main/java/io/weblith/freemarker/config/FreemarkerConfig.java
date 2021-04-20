package io.weblith.freemarker.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "weblith.freemarker", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public final class FreemarkerConfig {

    /**
     * Freemarker templates default suffix
     */
    @ConfigItem(defaultValue = ".ftlh")
    public String defaultTemplateSuffix;

}
