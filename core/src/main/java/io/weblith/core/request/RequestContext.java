package io.weblith.core.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.HttpRequest;

import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.ext.web.RoutingContext;
import io.weblith.core.i18n.LocaleHandler;
import io.weblith.core.i18n.Messages;
import io.weblith.core.scopes.FlashScope;
import io.weblith.core.scopes.SessionScope;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

@RequestScoped
public class RequestContext {

    // Replace with ResteasyContext ?
    private final Map<Class<?>, Object> requestScopedObjects = new HashMap<Class<?>, Object>();

    @ConfigProperty(name = "quarkus.http.root-path")
    String contextPath;

    @Inject
    FlashScope flashScope;

    @Inject
    SessionScope sessionScope;

    @Inject
    LocaleHandler localeHandler;

    @Inject
    Messages messages;

    @Inject
    RoutingContext routingContext;

    public String contextPath() {
        return contextPath;
    }

    public <T, T2 extends T> void seed(Class<T> clazz, T2 value) {
        // checkState(!requestScopedObjects.containsKey(clazz), "Object " + clazz.getName() + " already exist");
        // ResteasyContext.pushContext(clazz, value);
        requestScopedObjects.put(clazz, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        // checkState(requestScopedObjects.containsKey(clazz), "Object " + clazz.getName() + " not seeded as expected");
        return (T) requestScopedObjects.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> lookup(Class<T> clazz) {
        return Optional.ofNullable((T) requestScopedObjects.get(clazz));
    }

    public void clear() {
        requestScopedObjects.clear();
    }

    public FlashScope flash() {
        return this.flashScope;
    }

    public SessionScope session() {
        return this.sessionScope;
    }

    public SecurityIdentity identity() {
        Instance<SecurityIdentity> identity = CDI.current().select(SecurityIdentity.class);
        return identity.isResolvable() ? identity.get() : null;
    }

    public LocaleHandler locale() {
        return this.localeHandler;
    }

    public Messages messages() {
        return this.messages;
    }

    public Class<?> controller() {
        return request().getUri().getMatchedResources().get(0).getClass();
        //return null;
    }

    public HttpRequest request() {
        return ResteasyContext.getContextData(HttpRequest.class);
    }

    public UriInfo uriInfo() {
        return ResteasyContext.getContextData(UriInfo.class);
    }

    public Optional<String> getParameterValue(String key) {
        UriInfo uriInfo = ResteasyContext.getContextData(UriInfo.class);
        Optional<String> value = Optional.ofNullable(uriInfo.getQueryParameters().getFirst(key));
        if (!value.isPresent()) {
            value = Optional.ofNullable(uriInfo.getPathParameters().getFirst(key));
        }
        return value;
    }

    public Optional<String> getCookieValue(String key) {
        HttpHeaders httpHeaders = ResteasyContext.getContextData(HttpHeaders.class);
        if (httpHeaders != null && httpHeaders.getCookies().containsKey(key)) {
            return Optional.ofNullable(httpHeaders.getCookies().get(key).getValue());
        }
        return Optional.empty();
    }
}
