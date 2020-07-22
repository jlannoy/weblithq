package io.weblith.core.parameters.date;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.FIELD, ElementType.PARAMETER
})
public @interface DateTimeFormat {

    public static final String DEFAULT_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ";

    String value() default DEFAULT_DATE_TIME;
}