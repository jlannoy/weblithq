package io.weblith.freemarker;

import io.weblith.core.i18n.LocaleHandler;
import io.weblith.core.i18n.Messages;
import io.weblith.core.request.RequestContext;
import io.weblith.freemarker.directives.AuthenticityHiddenField;
import io.weblith.freemarker.directives.AuthenticityToken;
import io.weblith.freemarker.methods.I18nMethod;
import io.weblith.freemarker.methods.PrettyTimeMethod;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FreemarkerComponentsProvider {

    @Inject
    RequestContext requestContext;

    @Inject
    LocaleHandler localeHandler;

    @Inject
    Messages messages;

    public AuthenticityToken authenticityToken() {
        return new AuthenticityToken(requestContext);
    }

    public AuthenticityHiddenField authenticityHiddenField() {
        return new AuthenticityHiddenField(requestContext);
    }

    public I18nMethod i18nMethod() {
        return new I18nMethod(messages);
    }

    public PrettyTimeMethod prettyTimeMethod() {
        return new PrettyTimeMethod(localeHandler);
    }
}
