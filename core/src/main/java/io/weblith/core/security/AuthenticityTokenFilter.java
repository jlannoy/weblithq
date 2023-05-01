package io.weblith.core.security;

import java.io.IOException;

import io.quarkus.logging.Log;
import io.quarkus.security.ForbiddenException;
import io.weblith.core.request.RequestContext;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 * CSRF protection mechanism.
 */
@ApplicationScoped
@Priority(Priorities.AUTHORIZATION - 10)
public class AuthenticityTokenFilter implements ContainerRequestFilter {

    public static final String AUTHENTICITY_TOKEN = "authenticityToken";

    @Inject
    RequestContext context;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        MultivaluedMap<String, String> formParameters = context.request().getDecodedFormParameters();
        if (!context.session().getAuthenticityToken().equals(formParameters.getFirst(AUTHENTICITY_TOKEN))) {

            Log.infov("Untrusted CSRF calling {0}", context.request().getUri().getPath());
            Log.debugv(" Session = {0}", context.session().getAuthenticityToken());
            Log.debugv(" Param = {0}", context.getParameterValue(AUTHENTICITY_TOKEN).orElse(null));

            throw new ForbiddenException();
        }

    }

}
