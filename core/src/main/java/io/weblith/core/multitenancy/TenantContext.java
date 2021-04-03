package io.weblith.core.multitenancy;

import java.util.Objects;

import org.slf4j.MDC;

public final class TenantContext {

    public static final String MDC_TENANT = "tenant";

    private final static InheritableThreadLocal<String> currentTenantId = new InheritableThreadLocal<>();

    private final static InheritableThreadLocal<String> currentDomain = new InheritableThreadLocal<>();

    public static void begin(String tenantId, String domain) {
        Objects.requireNonNull(tenantId, "Undefined tenant");

        if (isActive()) {
            throw new IllegalStateException(String.format("Cannot set current tenant to %s, already set as %s", tenantId, currentTenantId.get()));
        }

        currentTenantId.set(tenantId);
        currentDomain.set(domain);
        MDC.put(MDC_TENANT, tenantId);
    }

    public static void end() {
        MDC.clear();
        currentTenantId.remove();
        currentDomain.remove();
    }

    public static void executeFor(String tenantId, Runnable runnable) {
        begin(tenantId, null);
        try {
            runnable.run();
        } finally {
            end();
        }
    }

    public static boolean isActive() {
        return currentTenantId.get() != null;
    }

    public static String id() {
        return currentTenantId.get();
    }

    public static String domain() {
        return currentDomain.get();
    }

}