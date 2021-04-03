package io.weblith.core.security;

import java.io.IOException;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.logging.Logger;

import io.quarkus.security.ForbiddenException;
import io.weblith.core.request.RequestContext;

/**
 * CSRF protection mechanism.
 */
@ApplicationScoped
@Priority(Priorities.AUTHORIZATION - 10)
public class AuthenticityTokenFilter implements ContainerRequestFilter {

    public static final String AUTHENTICITY_TOKEN = "authenticityToken";

    private final static Logger LOGGER = Logger.getLogger(AuthenticityTokenFilter.class);

    @Inject
    RequestContext context;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        MultivaluedMap<String, String> formParameters = context.request().getDecodedFormParameters();
        if (!context.session().getAuthenticityToken().equals(formParameters.getFirst(AUTHENTICITY_TOKEN))) {

            LOGGER.infov("Untrusted CSRF calling {0}", context.request().getUri().getPath());
            LOGGER.debugv(" Session = {0}", context.session().getAuthenticityToken());
            LOGGER.debugv(" Param = {0}", context.getParameterValue(AUTHENTICITY_TOKEN).orElse(null));

            throw new ForbiddenException();
        }

    }

}
