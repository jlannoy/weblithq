package io.weblith.core.multitenancy;

import javax.enterprise.context.NormalScope;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
@NormalScope
@Inherited
public @interface TenantScoped {

    /**
     * Supports inline instantiation of the {@link TenantScoped} annotation.
     */
    public final static class Literal extends AnnotationLiteral<TenantScoped> implements TenantScoped {

        public static final Literal INSTANCE = new Literal();

        private static final long serialVersionUID = 1L;

    }

}