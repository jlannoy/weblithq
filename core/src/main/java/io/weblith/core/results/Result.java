package io.weblith.core.results;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response.Status;

import io.weblith.core.request.RequestContext;
import io.weblith.core.router.annotations.Controller;

/**
 * Generic class for managing Jax-RS response production in Weblith {@link Controller} endpoints.
 */
public abstract class Result {

    public static interface RenderResponse {
        void write(final OutputStream outputStream) throws Exception;
    }

    public static interface ConfigureResponse {
        void filter(RequestContext requestContext, ContainerResponseContext responseContext);
    }

    public static final String NOCACHE_VALUE = "no-cache, no-store, max-age=0, must-revalidate";

    private final Map<String, Object> headers;

    private final List<NewCookie> cookies;

    private String contentType;

    private Charset charset;

    private Status status;

    private boolean includeScopesCookies;

    public Result(String contentType, Status status) {
        this.contentType = contentType;
        this.status = status;
        this.headers = new HashMap<String, Object>();
        this.cookies = new ArrayList<>();
        this.includeScopesCookies = true;
        this.charset = StandardCharsets.UTF_8;
    }

    public Status getStatus() {
        return status;
    }

    public Result status(Status status) {
        this.status = status;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public Result contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Charset getCharset() {
        return charset;
    }

    public Result charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public boolean isIncludeScopeCookies() {
        return includeScopesCookies;
    }

    public Result includeScopesCookies() {
        this.includeScopesCookies = true;
        return this;
    }

    public Result doNotIncludeScopesCookies() {
        this.includeScopesCookies = false;
        return this;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public Result addHeader(String headerName, Object headerContent) {
        headers.put(headerName, headerContent);
        return this;
    }

    public List<NewCookie> getCookies() {
        return cookies;
    }

    public Result addCookie(NewCookie cookie) {
        cookies.add(cookie);
        return this;
    }

    /**
     * Sets Cache-Control: no-cache, no-store Date: (current date) Expires: 1970
     */
    public Result doNotCacheContent() {
        addHeader(HttpHeaders.CACHE_CONTROL, NOCACHE_VALUE);
        addHeader(HttpHeaders.DATE, new Date());
        addHeader(HttpHeaders.EXPIRES, new Date(0L));
        return this;
    }

    public Result download(String name) {
        this.contentType("application/x-download").addHeader(HttpHeaders.CONTENT_DISPOSITION,
                String.format("attachment; filename=\"%s\"", name));
        return this;
    }

}
