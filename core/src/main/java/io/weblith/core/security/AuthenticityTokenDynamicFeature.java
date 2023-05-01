package io.weblith.core.security;

import java.lang.reflect.Method;

import io.weblith.core.config.WeblithConfig;
import io.weblith.core.router.annotations.Post;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AuthenticityTokenDynamicFeature implements DynamicFeature {

    @Inject
    protected WeblithConfig weblithConfig;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext configurable) {
        final Class declaring = resourceInfo.getResourceClass();
        final Method method = resourceInfo.getResourceMethod();

        if (declaring == null || method == null)
            return;

        if (weblithConfig.csrfProtected &&
                method.getAnnotation(Post.class) != null &&
                method.getAnnotation(NotCsrfProtected.class) == null &&
                declaring.getAnnotation(NotCsrfProtected.class) == null) {

            configurable.register(AuthenticityTokenFilter.class);

        }
    }
}
