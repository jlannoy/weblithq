package io.weblith.core.parameters.date;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class ParametersConverterProvider implements ParamConverterProvider {

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {

        if (Date.class.equals(rawType)) {
            return (ParamConverter<T>) createJavaDateParameterConverter(annotations);
        }

        if (LocalDate.class.equals(rawType)) {
            return (ParamConverter<T>) createLocalDateParameterConverter(annotations);
        }

        if (LocalDateTime.class.equals(rawType)) {
            return (ParamConverter<T>) createLocalDateTimeParameterConverter(annotations);
        }

        return null;
    }

    private JavaDateParameterConverter createJavaDateParameterConverter(final Annotation[] annotations) {
        final JavaDateParameterConverter dateParameterConverter = new JavaDateParameterConverter();

        for (Annotation annotation : annotations) {
            if (DateTimeFormat.class.equals(annotation.annotationType())) {
                dateParameterConverter.setCustomDateTimeFormat((DateTimeFormat) annotation);
            } else if (DateFormat.class.equals(annotation.annotationType())) {
                dateParameterConverter.setCustomDateFormat((DateFormat) annotation);
            }
        }

        return dateParameterConverter;
    }

    private LocalDateParameterConverter createLocalDateParameterConverter(final Annotation[] annotations) {
        final LocalDateParameterConverter dateParameterConverter = new LocalDateParameterConverter();

        for (Annotation annotation : annotations) {
            if (DateFormat.class.equals(annotation.annotationType())) {
                dateParameterConverter.setCustomDateFormat((DateFormat) annotation);
            }
        }

        return dateParameterConverter;
    }

    private LocalDateTimeParameterConverter createLocalDateTimeParameterConverter(final Annotation[] annotations) {
        final LocalDateTimeParameterConverter dateParameterConverter = new LocalDateTimeParameterConverter();

        for (Annotation annotation : annotations) {
            if (DateTimeFormat.class.equals(annotation.annotationType())) {
                dateParameterConverter.setCustomDateTimeFormat((DateTimeFormat) annotation);
            }
        }

        return dateParameterConverter;
    }
}