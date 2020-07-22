package io.weblith.core.form.parsing;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

public class EmptyStringAsNullDeserializer extends StdDeserializer<String> {

    private static final long serialVersionUID = 2995665640722871158L;

    public EmptyStringAsNullDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        String result = StringDeserializer.instance.deserialize(p, ctxt);
        return result.isBlank() ? null : result;

    }
}