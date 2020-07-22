package io.weblith.core.parameters.date;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface DateFormat {
 
  public static final String DEFAULT_DATE = "yyyy-MM-dd";
 
  String value() default DEFAULT_DATE;
}