
package io.weblith.core.router.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Get {

    /**
     * Allows to set up a path for routing the corresponding method.
     */
    String value() default Controller.DEFAULT_ROUTE;

}