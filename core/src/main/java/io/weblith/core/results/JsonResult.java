package io.weblith.core.results;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import io.weblith.core.request.RequestContext;

public class JsonResult extends AbstractResult<JsonResult> implements Result.ConfigureResponse {

    protected final Object toRender;

    public JsonResult(Object toRender) {
        super(JsonResult.class, MediaType.APPLICATION_JSON, Status.OK);
        this.toRender = toRender;
    }

    @Override
    public void configure(RequestContext requestContext, ContainerResponseContext responseContext) {

        // Delegate Json rendering to Resteasy
        responseContext.setStatus(getStatus().getStatusCode());
        responseContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, getContentType());
        responseContext.setEntity(toRender);

    }

}