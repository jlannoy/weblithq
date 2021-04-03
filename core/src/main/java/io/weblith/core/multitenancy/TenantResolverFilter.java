package io.weblith.core.multitenancy;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

@ApplicationScoped
@Priority(Priorities.AUTHENTICATION - 100)
public class TenantResolverFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
System.out.println("hey");
    }

}
