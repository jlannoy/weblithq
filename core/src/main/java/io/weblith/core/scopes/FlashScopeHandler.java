package io.weblith.core.scopes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.quarkus.logging.Log;
import io.weblith.core.config.FlashConfig;
import io.weblith.core.config.WeblithConfig;
import io.weblith.core.request.RequestContext;
import io.weblith.core.results.AbstractResult;

public class FlashScopeHandler implements FlashScope {

    protected final Map<String, String> currentRequestData;

    protected final Map<String, String> nextRequestData;

    protected final CookieBuilder cookieBuilder;

    protected final FlashConfig flashConfig;

    protected boolean preExistingFlashData;

    public FlashScopeHandler(WeblithConfig weblithConfiguration, RequestContext context) {

        FlashConfig f = this.flashConfig = weblithConfiguration.flash;
        this.cookieBuilder = new CookieBuilder(f.cookieName, f.cookieDomain, f.cookiePath, f.cookieSecure, f.cookieHttpsOnly, context.contextPath());

        this.currentRequestData = new HashMap<>();
        this.nextRequestData = new HashMap<>();

        try {
            context.getCookieValue(flashConfig.cookieName).ifPresent(v -> {
                this.preExistingFlashData = true;
                this.currentRequestData.putAll(CookieBuilder.decodeMap(v));
            });
        } catch (Exception e) {
            Log.warn("Unable to decode flash cookie value", e);
        }
    }

    @Override
    public void save(AbstractResult<?> result) throws IOException {

        if (nextRequestData.isEmpty()) {

            // empty existing cookie, if needed
            if (this.preExistingFlashData) {
                result.addCookie(cookieBuilder.remove(flashConfig.cookieName));
            }

        } else {

            // build a cookie with this flash data
            try {
                String flashData = CookieBuilder.encodeMap(nextRequestData);
                result.addCookie(cookieBuilder.build(flashConfig.cookieName, flashData, -1));
            } catch (Exception e) {
                Log.error("Encoding cookie exception - this should never happen", e);
            }

        }

    }

    @Override
    public void now(String key, String value) {
        currentRequestData.put(key, value);
    }

    @Override
    public void put(String key, String value) {
        currentRequestData.put(key, value);
        nextRequestData.put(key, value);
    }

    @Override
    public void error(String value) {
        put("error", value);
    }

    @Override
    public void warning(String value) {
        put("warning", value);
    }

    @Override
    public void info(String value) {
        put("info", value);
    }

    @Override
    public void success(String value) {
        put("success", value);
    }

    @Override
    public void keep(String key) {
        if (currentRequestData.containsKey(key)) {
            nextRequestData.put(key, currentRequestData.get(key));
        }
    }

    @Override
    public void keep() {
        nextRequestData.putAll(getCurrentRequestData());
    }

    @Override
    public Map<String, String> getCurrentRequestData() {
        return currentRequestData;
    }

    @Override
    public Map<String, String> getNextRequestData() {
        return nextRequestData;
    }

}