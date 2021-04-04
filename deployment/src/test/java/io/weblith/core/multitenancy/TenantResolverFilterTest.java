package io.weblith.core.multitenancy;

import io.weblith.core.config.TenantsConfig;
import io.weblith.core.config.WeblithConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNull;

public class TenantResolverFilterTest {

    WeblithConfig weblithConfig;

    @BeforeEach
    public void init() {
        this.weblithConfig = new WeblithConfig();
        this.weblithConfig.tenants = new TenantsConfig();
        this.weblithConfig.tenants.defaultTenant = "public";
        this.weblithConfig.tenants.domains = new HashMap<>();
        this.weblithConfig.tenants.domain = Optional.empty();
        this.weblithConfig.tenants.subdomains = Optional.empty();
    }

    @Test
    public void testDefaultTenant() {
        TenantResolverFilter filter = new TenantResolverFilter(weblithConfig);
        assertThat(filter.getApplicationTenants(), hasItem("public"));
        assertThat(filter.getApplicationTenants(), hasSize(1));

        assertThat(filter.identifyTenant("first-domain.com"), is(Optional.of("public")));
        assertThat(filter.identifyTenant("second-domain.com"), is(Optional.of("public")));
        assertThat(filter.identifyTenant("third-domain.com"), is(Optional.of("public")));
    }

    @Test
    public void testIdentifyingDomains() {
        weblithConfig.tenants.domains = new HashMap<>();
        weblithConfig.tenants.domains.put("first", "first-domain.com");
        weblithConfig.tenants.domains.put("second", "second-domain.com");
        weblithConfig.tenants.domains.put("third", "third-domain.com");

        TenantResolverFilter filter = new TenantResolverFilter(weblithConfig);
        assertThat(filter.getApplicationTenants(), hasItems("first", "second", "third"));
        assertThat(filter.getApplicationTenants(), hasSize(3));

        assertThat(filter.identifyTenant("first-domain.com"), is(Optional.of("first")));
        assertThat(filter.identifyTenant("second-domain.com"), is(Optional.of("second")));
        assertThat(filter.identifyTenant("third-domain.com"), is(Optional.of("third")));
        assertThat(filter.identifyTenant("fourth-domain.com"), is(Optional.empty()));
    }

    @Test
    public void testIdentifyingSubdomains() {
        weblithConfig.tenants.domain = Optional.of("domain.com");
        weblithConfig.tenants.subdomains = Optional.of(new ArrayList<>());
        weblithConfig.tenants.subdomains.get().add("first");
        weblithConfig.tenants.subdomains.get().add("second");
        weblithConfig.tenants.subdomains.get().add("third");

        TenantResolverFilter filter = new TenantResolverFilter(weblithConfig);
        assertThat(filter.getApplicationTenants(), hasItems("first", "second", "third"));
        assertThat(filter.getApplicationTenants(), hasSize(3));

        assertThat(filter.identifyTenant("first.domain.com"), is(Optional.of("first")));
        assertThat(filter.identifyTenant("second.domain.com"), is(Optional.of("second")));
        assertThat(filter.identifyTenant("third.domain.com"), is(Optional.of("third")));
        assertThat(filter.identifyTenant("fourth.domain.com"), is(Optional.empty()));
    }

    @Test
    public void testMixingDomainsAndSubdomains() {
        weblithConfig.tenants.domains = new HashMap<>();
        weblithConfig.tenants.domains.put("first", "first-domain.com");
        weblithConfig.tenants.domains.put("second", "second-domain.com");
        weblithConfig.tenants.domains.put("third", "third-domain.com");
        weblithConfig.tenants.domains.put("fourth", "fourth-domain.com");
        weblithConfig.tenants.domain = Optional.of("domain.com");
        weblithConfig.tenants.subdomains = Optional.of(new ArrayList<>());
        weblithConfig.tenants.subdomains.get().add("first");
        weblithConfig.tenants.subdomains.get().add("second");
        weblithConfig.tenants.subdomains.get().add("third");

        TenantResolverFilter filter = new TenantResolverFilter(weblithConfig);
        assertThat(filter.getApplicationTenants(), hasItems("first", "second", "third", "fourth"));
        assertThat(filter.getApplicationTenants(), hasSize(4));

        assertThat(filter.identifyTenant("first-domain.com"), is(Optional.of("first")));
        assertThat(filter.identifyTenant("second-domain.com"), is(Optional.of("second")));
        assertThat(filter.identifyTenant("third-domain.com"), is(Optional.of("third")));
        assertThat(filter.identifyTenant("fourth-domain.com"), is(Optional.of("fourth")));
        assertThat(filter.identifyTenant("first.domain.com"), is(Optional.of("first")));
        assertThat(filter.identifyTenant("second.domain.com"), is(Optional.of("second")));
        assertThat(filter.identifyTenant("third.domain.com"), is(Optional.of("third")));
        assertThat(filter.identifyTenant("fourth.domain.com"), is(Optional.empty()));
    }
}
