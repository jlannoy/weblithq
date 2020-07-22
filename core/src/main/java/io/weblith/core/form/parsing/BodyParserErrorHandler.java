package io.weblith.core.form.parsing;

import java.io.IOException;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

import io.weblith.core.form.Form;
import io.weblith.core.form.validating.Violation;
import io.weblith.core.request.RequestContext;
import io.weblith.core.security.AuthenticityTokenFilter;

public class BodyParserErrorHandler extends DeserializationProblemHandler {

    private final static Logger LOGGER = Logger.getLogger(BodyParserErrorHandler.class);

    private final RequestContext context;

    public BodyParserErrorHandler(RequestContext contextProvider) {
        this.context = contextProvider;
    }

    @Override
    public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser jp, JsonDeserializer<?> deserializer,
            Object beanOrClass, String propertyName)
            throws IOException, JsonProcessingException {

        if (!AuthenticityTokenFilter.AUTHENTICITY_TOKEN.equals(propertyName)) {
            LOGGER.debugv("Form mapping : Property with name '{0}' doesn't exist in class '{1}'",
                    propertyName, beanOrClass.getClass().getName());
        }
        return true;
    }

    @Override
    public Object handleWeirdNumberValue(DeserializationContext ctxt, Class<?> targetType, Number valueToConvert,
            String failureMsg) throws IOException {

        if (ctxt.getParser().currentName() != null) {
            context.get(Form.class).addViolation(build(ctxt, targetType, String.valueOf(valueToConvert), failureMsg));
            return null;
        } else {
            return NOT_HANDLED;
        }
    }

    @Override
    public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert,
            String failureMsg) throws IOException {

        if (ctxt.getParser().currentName() != null) {
            context.get(Form.class).addViolation(build(ctxt, targetType, valueToConvert, failureMsg));
            return null;
        } else {
            return NOT_HANDLED;
        }
    }

    @Override
    public Object handleWeirdKey(DeserializationContext ctxt, Class<?> rawKeyType, String keyValue, String failureMsg)
            throws IOException {
        return super.handleWeirdKey(ctxt, rawKeyType, keyValue, failureMsg);
    }

    @Override
    public Object handleWeirdNativeValue(DeserializationContext ctxt, JavaType targetType, Object valueToConvert, JsonParser p)
            throws IOException {

        if (ctxt.getParser().currentName() != null) {
            context.get(Form.class).addViolation(build(ctxt, targetType.getRawClass(), String.valueOf(valueToConvert), null));
            return null;
        } else {
            return NOT_HANDLED;
        }
    }

    @Override
    public Object handleUnexpectedToken(DeserializationContext ctxt, Class<?> targetType, JsonToken t, JsonParser p,
            String failureMsg) throws IOException {

        if (ctxt.getParser().currentName() != null) {
            context.get(Form.class).addViolation(build(ctxt, targetType, null, failureMsg));
            return null;
        } else {
            return NOT_HANDLED;
        }
    }

    private Violation build(DeserializationContext ctxt, Class<?> targetType, String value, String failureMsg)
            throws IOException {

        return new Violation("validation.is." + targetType.getSimpleName().toLowerCase() + ".violation",
                ctxt.getParser().currentName(), failureMsg, value);

    }

}