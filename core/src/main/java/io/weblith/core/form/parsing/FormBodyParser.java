package io.weblith.core.form.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.util.Types.ResteasyParameterizedType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.weblith.core.form.Form;
import io.weblith.core.request.RequestContext;

@SuppressWarnings("rawtypes")
@Provider
@ApplicationScoped
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class FormBodyParser implements MessageBodyReader<Form> {

    private final static Logger LOGGER = Logger.getLogger(FormBodyParser.class);

    private final RequestContext context;

    private final ObjectMapper objectMapper;

    @Inject
    public FormBodyParser(RequestContext contextProvider, BodyParserObjectMapperProvider provider) {
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
            form.setValue(this.parse((Class<?>) innerType, context.request().getDecodedFormParameters()));
            return form;

        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }

    }

    public <T> T parse(Class<T> classOfT, MultivaluedMap<String, String> map) {
        return this.parse(classOfT, parseFormData(map));
    }

    public <T> T parse(Class<T> classOfT, Map<String, Object> map) {

        LOGGER.debugv("Converting map : {0}", map);

        try {
            return this.objectMapper.convertValue(map, classOfT);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid form data", e);
        }

    }

    protected Map<String, Object> parseFormData(MultivaluedMap<String, String> formData) {
        Map<String, Object> parameters = Maps.newHashMap();
        formData.entrySet().forEach(entry -> {
            // Do not register empty single valued properties
            // Allows to keep related object as null value
            if (!entry.getValue().isEmpty() && (entry.getValue().size() > 1 ||
                    (entry.getValue().get(0) != null && !entry.getValue().get(0).isBlank()))) {
                put(parameters, entry.getKey(), entry.getValue());
            }
        });
        return parameters;
    }

    @SuppressWarnings("unchecked")
    public void put(Map<String, Object> map, String key, List<String> values) {
        String property = key;

        // Process any nested object property
        int index = key.indexOf('.');
        if (index > 0) {
            property = key.substring(0, index);
            key = key.substring(index + 1);

            // Nested property in an array of objects
            if (property.indexOf('[') > 0) {
                // TODO getting index in collection
                property = property.substring(0, property.indexOf('['));
                if (!map.containsKey(property)) {
                    List<Map<String, Object>> objects = Lists.newArrayList();
                    values.forEach(v -> objects.add(Maps.newHashMap()));
                    map.put(property, objects);
                }

                List<Map<String, Object>> objects = (List<Map<String, Object>>) map.get(property);
                for (int i = 0; i < values.size(); i++) {
                    put(objects.get(i), key, Arrays.asList(values.get(i)));
                }

                // Nested property of a single object
            } else {
                if (!map.containsKey(property)) {
                    map.put(property, Maps.newHashMap());
                }
                put((Map<String, Object>) map.get(property), key, values);
            }

            // Process multiple value
        } else if (values.size() > 1) {
            map.put(key, values);

            // Process single value, only if not empty (filter done before)
        } else {
            map.put(key, values.get(0));
        }
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Form.class.isAssignableFrom(type);
    }

}
