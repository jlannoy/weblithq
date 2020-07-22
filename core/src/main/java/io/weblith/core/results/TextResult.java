package io.weblith.core.results;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import io.weblith.core.results.Result.RenderResponse;

public class TextResult extends Result implements RenderResponse {

    protected final String content;

    public TextResult(String content) {
        super(MediaType.TEXT_PLAIN, Status.OK);
        this.content = content;
    }

    public TextResult(Exception exception) {
        this(exception.getMessage() != null ? exception.getMessage() : exception.toString());
    }

    public TextResult(Object content) {
        this(String.valueOf(content));
    }

    @Override
    public void write(OutputStream entityStream) throws Exception {

        try (OutputStreamWriter writer = new OutputStreamWriter(entityStream, this.getCharset())) {

            writer.write(content);
            writer.flush();

        } catch (Exception cause) {

            throw new WebApplicationException(cause);

        }
    }

}