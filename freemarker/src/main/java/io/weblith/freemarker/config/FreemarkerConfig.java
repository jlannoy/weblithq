package io.weblith.freemarker.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "weblith.freemarker", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public final class FreemarkerConfig {

    /**
     * Templates configuration.
     */
    public TemplateConfig template;

}
