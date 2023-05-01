package io.weblith.core.parameters.date;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ext.ParamConverter;

public class LocalDateTimeParameterConverter implements ParamConverter<LocalDateTime> {

    private DateTimeFormat customDateTimeFormat;

    public void setCustomDateTimeFormat(DateTimeFormat customDateTimeFormat) {
        this.customDateTimeFormat = customDateTimeFormat;
    }

    @Override
    public LocalDateTime fromString(String string) {
        try {
            return string == null || string.isBlank() ? null : LocalDateTime.parse(string, pattern());
        } catch (DateTimeParseException ex) {
            throw new BadRequestException(ex);
        }
    }

    @Override
    public String toString(LocalDateTime dateTime) {
        return dateTime.format(pattern());
    }

    private DateTimeFormatter pattern() {
        return DateTimeFormatter
                .ofPattern(customDateTimeFormat != null ? customDateTimeFormat.value() : DateTimeFormat.DEFAULT_DATE_TIME);
    }
}