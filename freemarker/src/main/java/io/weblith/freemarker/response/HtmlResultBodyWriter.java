package io.weblith.freemarker.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import org.jboss.logging.Log.

import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import io.weblith.core.request.RequestContext;
import io.weblith.freemarker.template.TemplateResolver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
@Produces(MediaType.TEXT_HTML)
public class HtmlResultBodyWriter implements MessageBodyWriter<HtmlResult> {

    private static final Log.Log.= Log.getLog.HtmlResultBodyWriter.class);

    @Inject
    TemplateResolver templateResolver;

    @Inject
    RequestContext context;

    @Override
    public boolean isWriteable(final Class<?> type,
            final Type genericType,
            final Annotation[] annotations,
            final MediaType mediaType) {
        return HtmlResult.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(HtmlResult result,
            final Class<?> type,
            final Type genericType,
            final Annotation[] annotations,
            final MediaType mediaType,
            final MultivaluedMap<String, Object> httpHeaders,
            final OutputStream entityStream)
            throws IOException, WebApplicationException {

        try (OutputStreamWriter writer = new OutputStreamWriter(entityStream, result.getCharset())) {

            fillWithCommonParameters(result);
            Template template = templateResolver.resolve(result);
            Log.debugv("Processing template {0} as HTML result", template.getName());
            template.process(result.getTemplateParameters(), writer);
            writer.flush();

        } catch (TemplateNotFoundException e) {
            Log.error(e.getMessage(), e);
            throw new FreemarkerRenderingException(e);

        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            throw new FreemarkerRenderingException(e);

        }
    }

    protected Map<String, Object> fillWithCommonParameters(HtmlResult result) {
        Map<String, Object> map = result.getTemplateParameters();

        map.put("hostname", context.request().getUri().getRequestUri().getHost());
        map.put("requestPath", context.request().getUri().getRequestUri().getPath());
        map.put("params", context.request().getUri().getQueryParameters());

        map.put("lang", context.locale().current().getLanguage());
        map.put("flash", context.flash().getCurrentRequestData());
        map.put("Identity", context.identity());

        return map;
    }

}
