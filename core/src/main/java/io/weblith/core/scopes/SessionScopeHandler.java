package io.weblith.core.scopes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jboss.logging.Logger;

import io.weblith.core.config.SessionConfig;
import io.weblith.core.config.WeblithConfig;
import io.weblith.core.request.RequestContext;
import io.weblith.core.results.AbstractResult;

/**
 * Default SessionScope implementation.
 */
public class SessionScopeHandler implements SessionScope {

    protected final static Logger LOGGER = Logger.getLogger(SessionScopeHandler.class);

    protected final static String AUTHENTICITY_KEY = "_auth";

    protected final static String TIMESTAMP_KEY = "_timestamp";

    protected final Map<String, String> data;

    protected final CookieBuilder cookieBuilder;

    protected final SessionConfig sessionConfig;

    protected boolean sessionDataChanged;

    public SessionScopeHandler(WeblithConfig weblithConfiguration, RequestContext context) {

        SessionConfig s = this.sessionConfig = weblithConfiguration.session;
        this.cookieBuilder = new CookieBuilder(s.cookieName, s.cookieDomain, s.cookiePath, s.cookieSecure, s.cookieHttpsOnly, context.contextPath());

        this.data = new HashMap<String, String>();
        try {
            context.getCookieValue(sessionConfig.cookieName).ifPresent(v -> {
                this.data.putAll(CookieBuilder.decryptMap(v, sessionConfig.secret));
            });
        } catch (Exception e) {
            LOGGER.warn("Unable to decode session cookie value", e);
        }

        if (!this.data.containsKey(AUTHENTICITY_KEY)) {
            put(AUTHENTICITY_KEY, UUID.randomUUID().toString());
        }
        if (!this.data.containsKey(TIMESTAMP_KEY)) {
            put(TIMESTAMP_KEY, Instant.now().toString());
        }
    }

    @Override
    public void save(AbstractResult<?> result) {

        if (data.isEmpty()) {

            // empty existing cookie, if needed
            if (this.sessionDataChanged) {
                LOGGER.debugv("Saving empty session cookie");
                result.addCookie(cookieBuilder.remove(sessionConfig.cookieName));
            }

        } else if (this.sessionDataChanged || this.shouldBeRenewed()) {

            // build a cookie with the session data
            try {
                LOGGER.debugv("Saving new session cookie (size {0})", data.size());

                put(TIMESTAMP_KEY, Instant.now().toString());
                String sessionData = CookieBuilder.encryptMap(data, sessionConfig.secret);

                result.addCookie(cookieBuilder.build(sessionConfig.cookieName, sessionData, (int) sessionConfig.expire.toSeconds()));
            } catch (Exception e) {
                LOGGER.error("Encoding cookie exception - this should never happen", e);
            }

        }
    }

    public boolean shouldBeRenewed() {
        return getCreationTime().plusMillis(sessionConfig.renewal.toMillis()).isBefore(Instant.now());
    }

    @Override
    public void put(String key, String value) {
        if(!data.containsKey(key) || (value != null && !value.equals(data.get(key)))) {
            this.sessionDataChanged = true;
            data.put(key, value);
        }
    }

    @Override
    public String get(String key) {
        return data.get(key);
    }

    @Override
    public Optional<String> lookup(String key) {
        return Optional.ofNullable(data.get(key));
    }

    @Override
    public Optional<String> remove(String key) {
        this.sessionDataChanged = true;
        return Optional.ofNullable(data.remove(key));
    }

    @Override
    public void invalidate() {
        this.sessionDataChanged = true;
        data.clear();
    }

    @Override
    public String getAuthenticityToken() {
        return get(AUTHENTICITY_KEY);
    }

    public Instant getCreationTime() {
        return Instant.parse(get(TIMESTAMP_KEY));
    }

    public Map<String, String> getData() {
        return data;
    }

}