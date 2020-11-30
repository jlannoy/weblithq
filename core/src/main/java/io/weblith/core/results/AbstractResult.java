package io.weblith.core.results;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response.Status;

import io.weblith.core.router.annotations.Controller;

/**
 * Generic class for managing JAX-RS response production in Weblith {@link Controller} endpoints.
 */
public abstract class AbstractResult<T extends AbstractResult<?>> implements Result {

    public static final String NOCACHE_VALUE = "no-cache, no-store, max-age=0, must-revalidate";

    private final Map<String, Object> headers;

    private final List<NewCookie> cookies;

    private String contentType;

    private Charset charset;

    private Status status;

    private boolean includeScopesCookies;

    // Keep a casted reference for easy method chaining
    protected final T self;

    protected AbstractResult(final Class<T> selfClass, Status status) {
        this(selfClass, null, status);
    }

    protected AbstractResult(final Class<T> selfClass, String contentType, Status status) {
        this.self = selfClass.cast(this);
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

    public T status(Status status) {
        this.status = status;
        return self;
    }

    public String getContentType() {
        return contentType;
    }

    public T contentType(String contentType) {
        this.contentType = contentType;
        return self;
    }

    public Charset getCharset() {
        return charset;
    }

    public T charset(Charset charset) {
        this.charset = charset;
        return self;
    }

    public boolean isIncludeScopeCookies() {
        return includeScopesCookies;
    }

    public T includeScopesCookies() {
        this.includeScopesCookies = true;
        return self;
    }

    public T doNotIncludeScopesCookies() {
        this.includeScopesCookies = false;
        return self;
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

    public T download(String name) {
        this.contentType("application/x-download").addHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", name));
        return self;
    }

}
