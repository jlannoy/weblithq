package io.weblith.core.results;

import java.io.OutputStream;

import javax.ws.rs.core.Response.Status;

import io.weblith.core.results.Result.RenderResponse;

public class RawResult extends AbstractResult<RawResult> implements RenderResponse {

    private final byte[] bytes;

    public RawResult(byte[] bytes, String mediaType) {
        super(RawResult.class, mediaType, Status.OK);
        this.bytes = bytes;
    }

    @Override
    public void write(OutputStream entityStream) throws Exception {
        entityStream.write(this.bytes);
    }

}