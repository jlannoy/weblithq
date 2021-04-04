package io.weblith.core.multitenancy;

import io.weblith.core.config.TenantsConfig;
import io.weblith.core.config.WeblithConfig;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
@Priority(Priorities.AUTHENTICATION - 100)
public class TenantResolverFilter implements ContainerRequestFilter, TenantHandler {

    TenantsConfig tenantsConfig;

    final Set<String> tenants;

    @Inject
    public TenantResolverFilter(WeblithConfig weblithConfig) {
        this.tenantsConfig = weblithConfig.tenants;

        HashSet<String> keys = new HashSet<>();
        keys.addAll(tenantsConfig.domains.keySet());
        keys.addAll(tenantsConfig.subdomains.keySet());
        this.tenants =  keys.stream().map(k -> k.toLowerCase()).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
               System.out.println(  requestContext.getUriInfo().getBaseUri()  );
    }

    public String validate(String value) {
        value = value.toLowerCase();
        return this.tenants.contains(value) ? value : null;
    }

    @Override
    public Set<String> getApplicationTenants() {
        return this.tenants;
    }


}
