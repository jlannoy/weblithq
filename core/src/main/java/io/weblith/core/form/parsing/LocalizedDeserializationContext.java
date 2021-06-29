package io.weblith.core.form.parsing;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Date;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;

import io.weblith.core.request.RequestContext;

public class LocalizedDeserializationContext extends DefaultDeserializationContext {

    private static final long serialVersionUID = 3301619033354341241L;

    private final static Logger LOGGER = Logger.getLogger(LocalizedDeserializationContext.class);

    private final RequestContext context;

    public LocalizedDeserializationContext(RequestContext context) {
        this(BeanDeserializerFactory.instance, context);
    }

    private LocalizedDeserializationContext(DefaultDeserializationContext src,
            DeserializationConfig config,
            RequestContext context) {
        super(src, config);
        this.context = context;
    }

    private LocalizedDeserializationContext(DeserializerFactory factory, RequestContext context) {
        super(factory, null);
        this.context = context;
    }

    private LocalizedDeserializationContext(DefaultDeserializationContext src,
            DeserializationConfig config,
            JsonParser parser,
            InjectableValues values,
            RequestContext context) {
        super(src, config, parser, values);
        this.context = context;
    }

    @Override
    public DefaultDeserializationContext with(DeserializerFactory factory) {
        return new LocalizedDeserializationContext(factory, this.context);
    }

    @Override
    public DefaultDeserializationContext createInstance(DeserializationConfig config,
            JsonParser parser,
            InjectableValues values) {
        return new LocalizedDeserializationContext(this, config, parser, values, this.context);
    }

    @Override
    public Object handleWeirdStringValue(Class<?> targetClass, String value, String msg, Object... msgArgs) throws IOException {
        try {
            if (targetClass == float.class || targetClass == Float.class) {
                return parseLocalizedNumber(value).floatValue();
            } else if (targetClass == double.class || targetClass == Double.class) {
                return parseLocalizedNumber(value).doubleValue();
            } else if (targetClass == LocalDate.class) {
                return parseLocalDate(value);
            } else if (targetClass == LocalTime.class) {
                return parseLocalTime(value);
            } else if (targetClass == LocalDateTime.class) {
                return parseLocalDateTime(value);
            }
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
        }
        return super.handleWeirdStringValue(targetClass, value, msg, msgArgs);
    }

    protected Number parseLocalizedNumber(String value) throws ParseException {
        return NumberFormat.getNumberInstance(context.locale().current()).parse(value.replaceAll("\\s", ""));
    }

    @Override
    public Date parseDate(String value) {
        try {
            return Date.from(parseLocalDateTime(value).atZone(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            // Try the date limited format (if only date)
        }
        if (!value.contains("T")) {
            try {
                return Date.from(parseLocalDate(value).atStartOfDay(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException e) {
                // Try the default format
            }
        }
        return super.parseDate(value);
    }

    protected LocalDate parseLocalDate(String value) {
        try {
            value = value.indexOf('T') > -1 ? value.substring(0, value.indexOf('T')) : value;
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            // Try the localized format
        }
        DateTimeFormatter format = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(context.locale().current());
        try {
            return LocalDate.parse(value, format);
        } catch (DateTimeParseException e) {
            LOGGER.debugv("Unable to parse '{0}' as a date, requiring ISO format or {1}", value,
                    format.toFormat().format(LocalDate.now()));
            throw e;
        }
    }

    protected LocalTime parseLocalTime(String value) {
        try {
            return LocalTime.parse(value, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException e) {
            // Try the localized format
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("H:mm").withLocale(context.locale().current());
        try {
            return LocalTime.parse(value, format);
        } catch (DateTimeParseException e) {
            LOGGER.debugv("Unable to parse '{0}' as a time, requiring ISO format or {1}", value,
                    format.toFormat().format(LocalTime.now()));
            throw e;
        }
    }

    protected LocalDateTime parseLocalDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            // Try the localized format
        }
        DateTimeFormatter format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(context.locale().current());
        try {
            return LocalDateTime.parse(value, format);
        } catch (DateTimeParseException e) {
            LOGGER.debugv("Unable to parse '{0}' as date and time, requiring ISO format or {1}", value,
                    format.toFormat().format(LocalDateTime.now()));
            throw e;
        }
    }

    @Override
    public DefaultDeserializationContext createDummyInstance(DeserializationConfig config) {
        return new LocalizedDeserializationContext(this, config, this.context);
    }

}