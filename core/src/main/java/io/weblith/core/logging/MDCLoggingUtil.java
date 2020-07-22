package io.weblith.core.logging;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.HttpHeaders;

import org.jboss.logging.MDC;
import org.jboss.resteasy.spi.HttpRequest;

import io.quarkus.security.identity.SecurityIdentity;

public class MDCLoggingUtil {

    private static final String USER_ANONYMOUS = "user.anonymous";
    private static final String USER_NAME = "user.name";
    private static final String CONTROLLER_CLASS = "controller.class";
    private static final String CONTROLLER_METHOD = "controller.method";
    private static final String PARAM_PREFIX = "param.";

    /* Taken from SLF4j Classic Constants for MDC */
    private static final String REQUEST_REMOTE_HOST_MDC_KEY = "req.remoteHost";
    private static final String REQUEST_USER_AGENT_MDC_KEY = "req.userAgent";
    private static final String REQUEST_REQUEST_URI = "req.requestURI";
    private static final String REQUEST_METHOD = "req.method";
    private static final String REQUEST_SOURCE = "req.source";
    private static final String REQUEST_REFERER = "req.referer";
    private static final String REQUEST_CONTENT_TYPE = "req.contentType";
    private static final String REQUEST_STATUS_CODE = "req.status";

    public static void putRequestDetails(HttpRequest request, ContainerResponseContext response) {
        MDC.put(REQUEST_REMOTE_HOST_MDC_KEY, request.getRemoteHost());
        MDC.put(REQUEST_REQUEST_URI, request.getUri().getRequestUri());
        MDC.put(REQUEST_METHOD, request.getHttpMethod());
        MDC.put(REQUEST_USER_AGENT_MDC_KEY, request.getHttpHeaders().getHeaderString(HttpHeaders.USER_AGENT));
        MDC.put(REQUEST_SOURCE, request.getHttpHeaders().getHeaderString("X-Forwarded-For"));
        MDC.put(REQUEST_REFERER, request.getHttpHeaders().getHeaderString("Referer"));
        MDC.put(REQUEST_CONTENT_TYPE, request.getHttpHeaders().getHeaderString(HttpHeaders.ACCEPT));
        MDC.put(REQUEST_STATUS_CODE, response.getStatus());
    }

    public static void putCurrentUser(SecurityIdentity identity) {
        MDC.put(USER_ANONYMOUS, identity.getPrincipal().getName());
        MDC.put(USER_NAME, identity.getPrincipal().getName());
    }

    public static void putCurrentRoute(Class<?> controllerClass, String method) {
        MDC.put(CONTROLLER_CLASS, controllerClass);
        MDC.put(CONTROLLER_METHOD, method);
    }

}
