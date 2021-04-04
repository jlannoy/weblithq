package io.weblith.core.multitenancy;

import java.util.Locale;
import java.util.Set;

public interface TenantHandler {

    String validate(String value);

    Set<String> getApplicationTenants();

}