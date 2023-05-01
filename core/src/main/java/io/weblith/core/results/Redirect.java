package io.weblith.core.results;

import java.net.URL;

import io.weblith.core.request.RequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response.Status;

public class Redirect extends AbstractResult<Redirect> implements AbstractResult.ConfigureResponse {

    private final String url;

    private String hashTag = null;

    private RedirectMessage message = null;

    public Redirect(String url) {
        super(Redirect.class, Status.SEE_OTHER);
        this.url = url;
    }

    public Redirect(URL url) {
        this(url.toExternalForm());
    }

    public Redirect withHashTag(String hashTag) {
        this.hashTag = '#' + hashTag.toLowerCase().replace(' ', '_');
        return this;
    }

    public Redirect withSuccess(String messageKey, Object... args) {
        this.message = new RedirectMessage("success", "success." + messageKey, args);
        return this;
    }

    public Redirect withError(String messageKey, Object... args) {
        this.message = new RedirectMessage("error", "error." + messageKey, args);
        return this;
    }

    public Redirect withWarning(String messageKey, Object... args) {
        this.message = new RedirectMessage("warning", "warning." + messageKey, args);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public String getHashTag() {
        return this.hashTag;
    }

    public RedirectMessage getMessage() {
        return this.message;
    }

    public static class RedirectMessage {
        public String type, key;
        public Object[] args;

        public RedirectMessage(String type, String key, Object[] args) {
            super();
            this.type = type;
            this.key = key;
            this.args = args;
        }

    }

    @Override
    public void configure(RequestContext requestContext, ContainerResponseContext responseContext) {

        String path = url != null ? url : "/";
        if (!path.startsWith("http") && !path.startsWith(requestContext.contextPath())) {
            path = requestContext.contextPath() + url;
        }
        if (hashTag != null) {
            path += hashTag;
        }

        if (message != null) {
            requestContext.flash().put(message.type, requestContext.messages().getWithDefault(message.key, message.key, message.args));
        }

        responseContext.setStatus(getStatus().getStatusCode());
        responseContext.getHeaders().putSingle(HttpHeaders.LOCATION, path);
        responseContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, "");
        responseContext.setEntity(null);

    }

}
