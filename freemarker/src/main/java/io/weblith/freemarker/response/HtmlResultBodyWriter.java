package io.weblith.freemarker.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import io.weblith.core.request.RequestContext;
import io.weblith.freemarker.template.TemplateResolver;
import org.jboss.logging.Logger;

@Provider
@ApplicationScoped
@Produces(MediaType.TEXT_HTML)
public class HtmlResultBodyWriter implements MessageBodyWriter<HtmlResult> {

    private static final Logger LOGGER = Logger.getLogger(HtmlResultBodyWriter.class);

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
            LOGGER.debugv("Processing template {0} as HTML result", template.getName());
            template.process(result.getTemplateParameters(), writer);
            writer.flush();

        } catch (TemplateNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new FreemarkerRenderingException(e);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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
