package io.weblith.core.parameters.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;

public class LocalDateParameterConverter implements ParamConverter<LocalDate> {

    private DateFormat customDateFormat;

    public void setCustomDateFormat(DateFormat customDateFormat) {
        this.customDateFormat = customDateFormat;
    }

    @Override
    public LocalDate fromString(String string) {
        try {
            return string == null || string.isBlank() ? null : LocalDate.parse(string, currentFormat());
        } catch (DateTimeParseException ex) {
            throw new BadRequestException(ex);
        }
    }

    @Override
    public String toString(LocalDate date) {
        return date.format(currentFormat());
    }

    private DateTimeFormatter currentFormat() {
        return DateTimeFormatter.ofPattern(customDateFormat != null ? customDateFormat.value() : DateFormat.DEFAULT_DATE);
    }
}