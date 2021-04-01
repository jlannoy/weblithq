package io.weblith.core.scopes;

import static javax.ws.rs.core.Cookie.DEFAULT_VERSION;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;

public class CookieBuilder {

    private final static String KEY_VALUE_SEP = "=";

    private final static String ENTRY_SEP = "&";

    private final String cookieName;

    private final String cookieDomain;

    private final String cookiePath;

    private final boolean cookieSecure;

    private final boolean cookieHttpsOnly;

    public CookieBuilder(String cookieName, Optional<String> cookieDomain, Optional<String> cookiePath, boolean cookieSecure, boolean cookieHttpsOnly, String contextPath) {
        this.cookieName = cookieName;
        this.cookieDomain = cookieDomain.orElse(null);
        this.cookiePath = cookiePath.orElse(contextPath);
        this.cookieSecure = cookieSecure;
        this.cookieHttpsOnly = cookieHttpsOnly;
    }

    // TODO Reuse Cipher as in PersistentLoginManager ??
    // private final BasicTextEncryptor textEncryptor;

    //        if (this.secretKey.isPresent()) {
    //            this.textEncryptor = new BasicTextEncryptor();
    //            this.textEncryptor.setPassword(this.secretKey.get());
    //        } else {
    //            logger.warn("No secret key configured for cookie encryption ; therefore session cookie will not be encrypted");
    //            this.textEncryptor = null;
    //        }

    public NewCookie remove(String name) {
        return new NewCookie(this.cookieName, "", this.cookiePath, this.cookieDomain, DEFAULT_VERSION, null, 0, null, false, false);
    }

    public NewCookie build(String name, String value, int maxAge) {
        return new NewCookie(this.cookieName, value, this.cookiePath, this.cookieDomain, DEFAULT_VERSION, null,
                maxAge, null, this.cookieSecure, this.cookieHttpsOnly);
    }

    public NewCookie build(String name, String value, int maxAge, Date expiry) {
        return new NewCookie(this.cookieName, value, this.cookiePath, this.cookieDomain, DEFAULT_VERSION, null,
                maxAge, expiry, this.cookieSecure, this.cookieHttpsOnly);
    }

    public static Map<String, String> decodeMap(String value) {

        Map<String, String> results = new HashMap<String, String>();
        if (value != null && !value.isBlank()) {
            for (String entry : value.split(ENTRY_SEP)) {
                String[] keyValue = entry.split(KEY_VALUE_SEP);
                if (keyValue.length == 2) {
                    results.put(decode(keyValue[0]), decode(keyValue[1]));
                }
            }
        }
        return results;

    }

    public static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    public static String encodeMap(Map<String, String> map) {
        if (map.isEmpty()) {
            return "";
        }
        return map.entrySet()
                .stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));
    }

    public static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public static String encryptMap(Map<String, String> map, String secret) {
        if (secret.isBlank()) {
            return encodeMap(map);
        } else {
            try {
                // return this.textEncryptor.encrypt(this.encode(map));
                return encodeMap(map);
            } catch (Exception e) {
                throw new WebApplicationException(e);
            }
        }
    }

    public static Map<String, String> decryptMap(String value, String secret) {
        if (secret.isBlank()) {
            return decodeMap(value);
        } else {
            // return this.decode(this.textEncryptor.decrypt(value));
            return decodeMap(value);
        }
    }

    /**
     * Constant time for same length String comparison, to prevent timing attacks
     */
    public static boolean safeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        } else {
            char equal = 0;
            for (int i = 0; i < a.length(); i++) {
                equal |= a.charAt(i) ^ b.charAt(i);
            }
            return equal == 0;
        }
    }

    public static void save(ContainerResponseContext responseContext, NewCookie cookie) {
        // Not working... responseContext.getCookies().put(cookie.getName(), cookie);
        responseContext.getHeaders().add(HttpHeaders.SET_COOKIE, cookie);
    }
}
