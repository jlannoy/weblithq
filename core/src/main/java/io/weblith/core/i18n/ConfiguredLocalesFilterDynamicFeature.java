package io.weblith.core.i18n;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;

public class ConfiguredLocalesFilterDynamicFeature implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext configurable) {
        configurable.register(ConfiguredLocalesFilter.class);
    }

}
