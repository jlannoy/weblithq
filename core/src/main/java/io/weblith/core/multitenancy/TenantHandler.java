package io.weblith.core.multitenancy;

import java.util.Locale;
import java.util.Set;

public interface TenantHandler {

    Set<String> getApplicationTenants();

}