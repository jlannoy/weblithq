package io.weblith.core.results;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import io.weblith.core.results.Result.RenderResponse;

/**
 * Convenience class for streaming data as a {@link Result}. Can be build either from a {@link File} or from a {@link URL}. In
 * both cases the content type will be guessed based on the file name.
 */
public class StreamResult extends Result implements RenderResponse {

    private final URL url;

    private boolean disableHttpCache;

    public StreamResult(URL contentUrl) {
        super(null, Status.OK);
        this.url = contentUrl;
        this.initialize();
    }

    public StreamResult(File file) {
        super(null, Status.OK);
        try {
            this.url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new WebApplicationException(e, Status.NOT_FOUND);
        }
        this.initialize();
    }

    protected void initialize() {
        String contentType = URLConnection.guessContentTypeFromName(url.getFile());
        this.contentType(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM);
        this.disableHttpCache = false;
        this.doNotIncludeScopesCookies();
    }

    public URL getUrl() {
        return url;
    }

    public boolean isHttpCacheEnabled() {
        return !disableHttpCache;
    }

    public StreamResult disableHttpCache() {
        this.disableHttpCache = true;
        return this;
    }

    public StreamResult download() {
        return (StreamResult) download(url.getFile());
    }

    @Override
    public void write(OutputStream entityStream) throws Exception {
        try (InputStream inputStream = url.openConnection().getInputStream()) {
            inputStream.transferTo(entityStream);
        }
    }

}
