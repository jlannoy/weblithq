package io.weblith.core.scopes;

import java.util.Optional;

import io.weblith.core.results.Result;

public interface SessionScope {

    public String getAuthenticityToken();

    /**
     * To finally send this session to the user this method has to be called. It basically serializes the session into
     * the header of the response.
     */
    public void save(Result result);

    /**
     * Puts key / value into the session. PLEASE NOTICE: If value == null the key will be removed!
     */
    public void put(String key, String value);

    /**
     * Returns the value of the key or null.
     */
    public String get(String key);
    
    /**
     * Returns the value of the key.
     */
    public Optional<String> lookup(String key);

    /**
     * Removes the value of the key and returns the value or null.
     */
    public Optional<String> remove(String key);
    
    public void invalidate();

}
