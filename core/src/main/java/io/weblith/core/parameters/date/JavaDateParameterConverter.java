package io.weblith.core.parameters.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;

public class JavaDateParameterConverter implements ParamConverter<Date> {

    private DateTimeFormat customDateTimeFormat;

    private DateFormat customDateFormat;

    public void setCustomDateFormat(DateFormat customDateFormat) {
        this.customDateFormat = customDateFormat;
    }

    public void setCustomDateTimeFormat(DateTimeFormat customDateTimeFormat) {
        this.customDateTimeFormat = customDateTimeFormat;
    }

    @Override
    public Date fromString(String string) {
        try {
            return currentFormat().parse(string);
        } catch (ParseException ex) {
            throw new BadRequestException(ex);
        }
    }

    private SimpleDateFormat currentFormat() {
        String format = DateTimeFormat.DEFAULT_DATE_TIME;

        if (customDateFormat != null) {
            format = customDateFormat.value();
        } else if (customDateTimeFormat != null) {
            format = customDateTimeFormat.value();
        }

        return new SimpleDateFormat(format);
    }

    @Override
    public String toString(Date date) {
        return currentFormat().format(date);
    }
}