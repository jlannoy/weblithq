package io.weblith.core.form.validating;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolverContext;

import io.weblith.core.request.RequestContext;

/**
 * Used by {@link HibernateValidator} to retrieve the Locale to use for translating constraint violation messages.
 */
@Singleton
public class RequestContextLocaleResolver implements LocaleResolver {

    @Inject
    RequestContext requestContext;

    @Override
    public Locale resolve(LocaleResolverContext context) {
        return requestContext.locale().current();
    }

}
