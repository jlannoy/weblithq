package io.weblith.core.scopes;

import java.io.IOException;
import java.util.Map;

import io.weblith.core.results.Result;

/**
 * Flash Scope consists of two kinds of data: "current" and "outgoing". Current data will only exist for the current
 * request. Outgoing data will exist for the current and next request. Neither should be considered secure or encrypted.
 * Its useful for communicating error messages or form submission results.
 * 
 * A FlashScope is i18n aware and the values will be looked up for i18n translations by template engines that support
 * it.
 * 
 * If the Flash Scope has outgoing data then a cookie will be sent to the client and will be valid on the next request.
 * Stuff in a flash cookie gets deleted after the next request.
 * 
 * If an incoming request has a flash cookie then the data from it will be loaded as "current" flash data. Unless you
 * keep() those keys that data will only be valid for the current request.
 */
public interface FlashScope {

    /**
     * Puts the key and value into only the "current" flash data. Will NOT be written as a cookie and will only exist
     * for the current request. Accessible via ${flash.key} in your html templating engine.
     */
    void now(String key, String value);

    /**
     * Puts the key and value into both "current" and "outgoing" flash data. Will be written as a cookie and available
     * in the current and next request. If you only need the value in your current request its a good idea to use the
     * <code>now()</code> method instead so you can eliminate the possibility of showing unexpected flash messages on
     * the next request :-).
     */
    void put(String key, String value);

    /**
     * Will copy the "current" flash data specified by the key into the "outgoing" flash data.
     */
    void keep(String key);

    /**
     * Copies all "current" flash data into the "outgoing" flash data.
     */
    void keep();

    /**
     * Same as calling <code>flash.put("error", "your value");</code>. The value will be added to both "current" and
     * "outgoing" flash data.
     */
    void error(String value);

    /**
     * Same as calling <code>flash.put("warning", "your value");</code>. The value will be added to both "current" and
     * "outgoing" flash data.
     */
    void warning(String value);

    /**
     * Same as calling <code>flash.put("info", "your value");</code>. The value will be added to both "current" and
     * "outgoing" flash data.
     */
    void info(String value);

    /**
     * Same as calling <code>flash.put("success", "your value");</code>. The value will be added to both "current" and
     * "outgoing" flash data.
     */
    void success(String value);

    Map<String, String> getCurrentRequestData();

    Map<String, String> getNextRequestData();

    void save(Result result) throws IOException;

}
