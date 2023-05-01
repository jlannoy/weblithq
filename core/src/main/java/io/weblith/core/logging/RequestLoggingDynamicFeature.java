package io.weblith.core.logging;

import java.lang.reflect.Method;

import io.weblith.core.router.annotations.Get;
import io.weblith.core.router.annotations.Post;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RequestLoggingDynamicFeature implements DynamicFeature {

    // TODO Check enabled property here
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext configurable) {
        final Class declaring = resourceInfo.getResourceClass();
        final Method method = resourceInfo.getResourceMethod();

        if (declaring == null || method == null)
            return;

        if ((method.getAnnotation(Post.class) != null || method.getAnnotation(Get.class) != null) &&
                method.getAnnotation(NotLogged.class) == null &&
                declaring.getAnnotation(NotLogged.class) == null) {

            configurable.register(RequestLoggingFilter.class);

        }
    }
}
