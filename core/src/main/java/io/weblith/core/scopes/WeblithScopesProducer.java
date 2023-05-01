package io.weblith.core.scopes;

import io.quarkus.arc.DefaultBean;
import io.weblith.core.config.WeblithConfig;
import io.weblith.core.i18n.LocaleHandler;
import io.weblith.core.i18n.Messages;
import io.weblith.core.i18n.ResourceBundleMessages;
import io.weblith.core.request.RequestContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class WeblithScopesProducer {

    @Inject
    WeblithConfig weblithConfiguration;

    @DefaultBean
    @Produces
    @RequestScoped
    public FlashScope flash(RequestContext context) {
        return new FlashScopeHandler(weblithConfiguration, context);
    }

    @DefaultBean
    @Produces
    @RequestScoped
    public SessionScope session(RequestContext context) {
        return new SessionScopeHandler(weblithConfiguration, context);
    }

    @DefaultBean
    @Produces
    @ApplicationScoped
    public Messages messages(LocaleHandler localeHandler) {
        return new ResourceBundleMessages(localeHandler, weblithConfiguration);
    }

}
