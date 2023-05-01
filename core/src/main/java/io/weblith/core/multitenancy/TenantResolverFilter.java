package io.weblith.core.multitenancy;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.quarkus.logging.Log;
import io.weblith.core.config.TenantsConfig;
import io.weblith.core.config.WeblithConfig;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Priority(Priorities.AUTHENTICATION - 100)
public class TenantResolverFilter implements ContainerRequestFilter, TenantHandler {

    final Set<String> tenants;

    final Map<String, String> domains;

    @Inject
    public TenantResolverFilter(WeblithConfig weblithConfig) {
        this.domains = buildDomainsMapping(weblithConfig.tenants);
        this.tenants = this.domains.isEmpty()
                ? Collections.unmodifiableSet(Set.of(weblithConfig.tenants.defaultTenant))
                : this.domains.values().stream().collect(Collectors.toUnmodifiableSet());

        TenantScopeInjectableContext.init(this.tenants);
        Log.debugv("Domains map initialized with : {0}", this.domains);
    }

    protected Map<String, String> buildDomainsMapping(TenantsConfig tenantsConfig) {
        Map<String, String> domains = new HashMap<>();

        if (tenantsConfig.domain.isPresent() && tenantsConfig.subdomains.isPresent()) {
            for (String subdomain : tenantsConfig.subdomains.get()) {
                domains.put(String.format("%s.%s", subdomain, tenantsConfig.domain.get()), subdomain);
            }
        }

        if (!tenantsConfig.domains.isEmpty()) {
            domains.putAll(tenantsConfig.domains
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
        }

        return domains;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String requestedDomain = getDomain(requestContext);

        identifyTenant(requestedDomain).ifPresentOrElse(id -> {
            TenantContext.begin(id, requestedDomain);
        }, () -> {
            requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "No corresponding tenant").build());
        });
    }

    protected Optional<String> identifyTenant(String requestedDomain) {
        // If there is no specific tenant defined
        if (domains.isEmpty()) {
            return Optional.of(tenants.iterator().next());
        }

        // Check for full domain name definition
        if (domains.containsKey(requestedDomain)) {
            return Optional.of(domains.get(requestedDomain));
        }

        return Optional.empty();
    }

    protected String getDomain(ContainerRequestContext requestContext) {
        return requestContext.getUriInfo().getBaseUri().getHost().toLowerCase();
    }

    @Override
    public String validate(String value) {
        value = value.toLowerCase();
        return this.tenants.contains(value) ? value : null;
    }

    @Override
    public Set<String> getApplicationTenants() {
        return this.tenants;
    }

}
