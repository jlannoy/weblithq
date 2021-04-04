package io.weblith.core.multitenancy;

import io.weblith.core.config.TenantsConfig;
import io.weblith.core.config.WeblithConfig;
import io.weblith.core.i18n.ConfiguredLocalesFilter;
import org.jboss.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@Priority(Priorities.AUTHENTICATION - 100)
public class TenantResolverFilter implements ContainerRequestFilter, TenantHandler {

    protected static final Logger LOGGER = Logger.getLogger(TenantResolverFilter.class);

    final Set<String> tenants;

    final Map<String, String> domains;

    @Inject
    public TenantResolverFilter(WeblithConfig weblithConfig) {
        this.domains = buildDomainsMapping(weblithConfig.tenants);
        this.tenants = this.domains.isEmpty()
                ? Collections.unmodifiableSet(Set.of(weblithConfig.tenants.defaultTenant))
                : this.domains.values().stream().collect(Collectors.toUnmodifiableSet());

        LOGGER.debugv("Domains map initialized with : {0}", this.domains);
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

    public String validate(String value) {
        value = value.toLowerCase();
        return this.tenants.contains(value) ? value : null;
    }

    @Override
    public Set<String> getApplicationTenants() {
        return this.tenants;
    }


}
