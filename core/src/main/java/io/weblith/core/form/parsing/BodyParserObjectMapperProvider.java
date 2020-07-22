package io.weblith.core.form.parsing;

import java.text.SimpleDateFormat;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import io.weblith.core.request.RequestContext;

@ApplicationScoped
public class BodyParserObjectMapperProvider {

    private final RequestContext context;


    // private final Set<com.fasterxml.jackson.databind.Module> customModules;

    @Inject
    public BodyParserObjectMapperProvider(RequestContext context) {
        super();
        this.context = context;
        // this.customModules = customModules;
    }
    
    public final static String RFC3339 = "yyyy-MM-dd'T'HH:mm:ssX";

    public ObjectMapper get() {
        ObjectMapper objectMapper = new ObjectMapper(null, null, new LocalizedDeserializationContext(context));

        // By default, serialize date according to the RFC3339 form
        objectMapper.setDateFormat(new SimpleDateFormat(RFC3339));
        objectMapper.setSerializationInclusion(Include.ALWAYS);
        
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(createCustomDeserializersModule());
        // customModules.forEach(module -> objectMapper.registerModule(module));

        objectMapper.addHandler(new BodyParserErrorHandler(this.context));
        return objectMapper;
    }

    private SimpleModule createCustomDeserializersModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new EmptyStringAsNullDeserializer());
        return module;
    }
}
