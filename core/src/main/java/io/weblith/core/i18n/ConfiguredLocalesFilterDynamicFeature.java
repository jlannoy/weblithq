package io.weblith.core.i18n;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

public class ConfiguredLocalesFilterDynamicFeature implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext configurable) {
        configurable.register(ConfiguredLocalesFilter.class);
    }

}
