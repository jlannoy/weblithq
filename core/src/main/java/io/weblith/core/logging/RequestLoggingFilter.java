package io.weblith.core.logging;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.weblith.core.config.WeblithConfiguration;
import io.weblith.core.request.RequestContext;

@ApplicationScoped
@Priority(Priorities.USER + 1000)
public class RequestLoggingFilter implements ContainerResponseFilter {

    private final static Logger LOGGER = Logger.getLogger(RequestLoggingFilter.class);

    @Inject
    private ObjectMapper jsonMapper;

    @Inject
    RequestContext context;

    @Inject
    WeblithConfiguration config;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {

        if (!config.requestLogs.enabled) {
            return;
        }

        try {

            if (config.requestLogs.requestDetails) {
                MDCLoggingUtil.putRequestDetails(context.request(), responseContext);
            }

            if (config.requestLogs.requestUser && context.identity() != null) {
                MDCLoggingUtil.putCurrentUser(context.identity());
            }

            if (config.requestLogs.requestParameters) {
                // LOGGER.infov("{0}.{1}() {2}", context.controller(), null,
                LOGGER.infov("{0} {1} {2}", context.request().getHttpMethod(), context.request().getUri().getPath(),
                        jsonMapper.writeValueAsString(getParameters()));
            } else {
                // LOGGER.infov("{0}.{1}() - {2}", context.controller(), null,
                LOGGER.infov("{0} {1} {2}", context.request().getHttpMethod(), context.request().getUri().getPath(),
                        responseContext.getStatus());
            }

        } catch (JsonProcessingException e) {
            //LOGGER.infov("{0}.{1}() *** {2}", context.controller(), null, 
            LOGGER.infov("{0} {1} *** {2}", context.request().getHttpMethod(), context.request().getUri().getPath(),
                    e.getMessage());
        }

    }

    protected Map<String, Object> getParameters() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        if (context.request().formParametersRead()) {
            params.putAll(context.request().getDecodedFormParameters());
        }
        params.putAll(context.request().getUri().getQueryParameters());
        params.putAll(context.request().getUri().getPathParameters());

        Map<String, Object> parameters = new TreeMap<>();
        params.forEach((key, value) -> {
            if (key.contains("authenticityToken")) {
                // skip
            } else if (key.contains("password")) {
                parameters.put(key, "***");
            } else if (value.size() > 1) {
                parameters.put(key, value.toArray(String[]::new));
            } else if (value.size() == 1) {
                parameters.put(key, value.get(0).length() > 255 ? "***" : value.get(0));
            }
        });

        return parameters;

    }

}
