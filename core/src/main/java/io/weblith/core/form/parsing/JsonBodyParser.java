package io.weblith.core.form.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.jboss.resteasy.spi.util.Types.ResteasyParameterizedType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.weblith.core.form.Form;
import io.weblith.core.request.RequestContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;

@SuppressWarnings("rawtypes")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
public class JsonBodyParser implements MessageBodyReader<Form> {

    private final RequestContext context;

    private final ObjectMapper objectMapper;

    @Inject
    public JsonBodyParser(RequestContext contextProvider, BodyParserObjectMapperProvider provider) {
        this.context = contextProvider;
        this.objectMapper = provider.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Form readFrom(Class<Form> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {

        try {

            Type innerType = ((ResteasyParameterizedType) genericType).getActualTypeArguments()[0];

            Form form = Form.of((Class<?>) innerType);
            context.seed(Form.class, form);
            form.setValue(this.parse((Class<?>) innerType, entityStream));
            return form;

        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(e);
        }

    }

    public <T> T parse(Class<T> classOfT, InputStream entityStream)
            throws JsonParseException, JsonMappingException, IOException {
        return this.objectMapper.readValue(entityStream, classOfT);
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Form.class.isAssignableFrom(type);
    }

}
