package io.weblith.core.multitenancy;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TenantResolverFilterDynamicFeature implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext configurable) {
        configurable.register(TenantResolverFilter.class);
        configurable.register(TenantCleanerFilter.class);
    }

}
