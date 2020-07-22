
package io.weblith.core.router.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

    public final static String DEFAULT_ROUTE = "<<default>>";
    
    /**
     * Allows to set up a path prefix for routing this controller methods.
     */
    String value() default DEFAULT_ROUTE;

}