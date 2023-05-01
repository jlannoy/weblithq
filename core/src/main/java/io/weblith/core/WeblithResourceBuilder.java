package io.weblith.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.jboss.resteasy.spi.metadata.MethodParameter;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.metadata.Parameter.ParamType;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;

import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.core.router.annotations.Post;
import jakarta.ws.rs.core.MediaType;

public class WeblithResourceBuilder extends ResourceBuilder {

    private final static String CONTROLLER_SUFFIX = "Controller";

    @Override
    public Class<? extends Annotation> getCorrespondingRootAnnotation() {
        return Controller.class;
    }

    @Override
    protected ResourceClassBuilder createResourceClassBuilder(Class<?> clazz) {
        Controller controllerAnnotation = clazz.getAnnotation(Controller.class);

        if (controllerAnnotation == null) {
            // Should not happen as we pre-validate @Controller presence
            return new WeblithResourceClassBuilder(clazz, "/");
        }

        String path = controllerAnnotation.value();
        if (Controller.DEFAULT_ROUTE.equals(path)) {
            path = clazz.getSimpleName();
            if (clazz.getSimpleName().lastIndexOf(CONTROLLER_SUFFIX) > 0) {
                path = path.substring(0, clazz.getSimpleName().lastIndexOf(CONTROLLER_SUFFIX));
            }
        }
        return new WeblithResourceClassBuilder(clazz, path);
    }

    @Override
    protected void processMethod(boolean isLocator,
            ResourceClassBuilder resourceClassBuilder,
            Class<?> root,
            Method implementation) {
        Method method = getAnnotatedMethod(root, implementation);

        if (method == null) {
            return;
        }

        ResourceMethodBuilder resourceMethodBuilder = resourceClassBuilder.method(implementation, method);
        boolean buildPath = false;
        StringBuilder path = new StringBuilder();

        Get get;
        if ((get = method.getAnnotation(Get.class)) != null) {
            resourceMethodBuilder.get();
            buildPath = Controller.DEFAULT_ROUTE.equals(get.value());
            path.append(buildPath ? method.getName() : get.value());
        }

        Post post;
        if (get == null && (post = method.getAnnotation(Post.class)) != null) {
            resourceMethodBuilder.post();
            buildPath = Controller.DEFAULT_ROUTE.equals(post.value());
            path.append(buildPath ? method.getName() : post.value());
        }

        // handleProduces(resourceClassBuilder, method, resourceMethodBuilder);
        // handleConsumes(resourceClassBuilder, method, resourceMethodBuilder);

        for (int i = 0; i < resourceMethodBuilder.getLocator().getParams().length; i++) {
            resourceMethodBuilder.param(i).fromAnnotations();

            Parameter parameter = resourceMethodBuilder.param(i).getParameter();
            if (buildPath && ParamType.PATH_PARAM.equals(parameter.getParamType())) {
                path.append("/{").append(parameter.getParamName()).append("}");
            }
        }

        // resourceMethodBuilder.consumes(MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA);
        resourceMethodBuilder.produces(MediaType.TEXT_HTML);
        resourceMethodBuilder.path(path.toString());
        resourceMethodBuilder.buildMethod();

    }

//        private void handleConsumes(ResourceClassBuilder resourceClassBuilder, Method method,
//                ResourceMethodBuilder resourceMethodBuilder) {
//            final RequestMappingData requestMapping = getRequestMapping(resourceClassBuilder,
//                    method);
//            if (requestMapping != null && requestMapping.getConsumes().length > 0)
//                resourceMethodBuilder.consumes(requestMapping.getConsumes());
//        }
//    
//        private void handleProduces(ResourceClassBuilder resourceClassBuilder, Method method,
//                ResourceMethodBuilder resourceMethodBuilder) {
//            final RequestMappingData requestMapping = getRequestMapping(resourceClassBuilder,
//                    method);
//            if (requestMapping != null && requestMapping.getProduces().length > 0)
//                resourceMethodBuilder.produces(requestMapping.getProduces());
//            else {
//                if (!String.class.equals(method.getReturnType()) && !void.class.equals(method.getReturnType())) {
//                    resourceMethodBuilder.produces(MediaType.APPLICATION_JSON_TYPE);
//                }
//            }
//        }

    @Override
    public Method getAnnotatedMethod(final Class<?> root, final Method implementation) {
        if (implementation.isSynthetic()) {
            return null;
        }

        if (implementation.isAnnotationPresent(Get.class) || implementation.isAnnotationPresent(Post.class)) {
            return implementation;
        }

        // // Check super-classes for inherited annotations
        // for (Class<?> clazz = implementation.getDeclaringClass().getSuperclass(); clazz != null; clazz =
        // clazz.getSuperclass()) {
        // final Method overriddenMethod = Types.findOverriddenMethod(implementation.getDeclaringClass(), clazz,
        // implementation);
        // if (overriddenMethod != null) {
        // return overriddenMethod;
        // }
        // }
        //
        // // Check implemented interfaces for inherited annotations
        // for (Class<?> clazz = root; clazz != null; clazz = clazz.getSuperclass()) {
        // Method overriddenMethod = null;
        //
        // for (Class<?> classInterface : clazz.getInterfaces()) {
        // final Method overriddenInterfaceMethod = Types.getImplementedInterfaceMethod(root, classInterface,
        // implementation);
        // if (overriddenInterfaceMethod == null) {
        // continue;
        // }
        // // Ensure no redefinition by peer interfaces (ambiguous) to preserve logic found in
        // // original implementation
        // if (overriddenMethod != null && !overriddenInterfaceMethod.equals(overriddenMethod)) {
        // throw new RuntimeException(Messages.MESSAGES.ambiguousInheritedAnnotations(implementation));
        // }
        //
        // overriddenMethod = overriddenInterfaceMethod;
        // }
        //
        // if (overriddenMethod != null) {
        // return overriddenMethod;
        // }
        // }

        return null;
    }

    private static class WeblithResourceClassBuilder extends ResourceClassBuilder {

        WeblithResourceClassBuilder(final Class<?> root, final String path) {
            super(root, path);
        }

        @Override
        public ResourceMethodBuilder method(Method method, Method annotatedMethod) {
            return new WeblithResourceMethodBuilder(this, method, annotatedMethod);
        }
    }

    private static class WeblithResourceMethodBuilder extends ResourceMethodBuilder {

        WeblithResourceMethodBuilder(final ResourceClassBuilder resourceClassBuilder,
                final Method method,
                final Method annotatedMethod) {
            super(resourceClassBuilder, method, annotatedMethod);
        }

        @Override
        public ResourceMethodParameterBuilder param(int i) {
            return new WeblithResourceMethodParameterBuilder(this, getLocator().getParams()[i]);
        }
    }

    private static class WeblithResourceMethodParameterBuilder extends ResourceMethodParameterBuilder {

        WeblithResourceMethodParameterBuilder(final ResourceMethodBuilder method, final MethodParameter param) {
            super(method, param);
        }

        @Override
        protected void doFromAnnotations() {
            // final Parameter parameter = getParameter();
            // Annotation[] annotations = parameter.getAnnotations();

            super.doFromAnnotations();

            // PathParam uriParam;
            // if ((uriParam = findAnnotation(annotations, PathParam.class)) != null) {
            // parameter.setParamType(Parameter.ParamType.PATH_PARAM);
            // parameter.setParamName(uriParam.value().isBlank() ? this.defaultName : uriParam.value());
            // parameter.setEncoded(true);
            // }

            //            RequestParam requestParam;
            //            RequestHeader header;
            //            MatrixVariable matrix;
            //            PathVariable uriParam;
            //            CookieValue cookie;
            //            FormParam formParam;
            //            Form form;
            //            Suspended suspended;
            //
            //            if ((requestParam = findAnnotation(annotations, RequestParam.class)) != null) {
            //                parameter.setParamType(Parameter.ParamType.QUERY_PARAM);
            //                parameter.setParamName(requestParam.name());
            //                if (parameter.getParamName().isEmpty() && !requestParam.value().isEmpty()) {
            //                    parameter.setParamName(requestParam.value());
            //                }
            //                if (!requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
            //                    parameter.setDefaultValue(requestParam.defaultValue());
            //                }
            //                parameter.setEncoded(true);
            //            } else if ((header = findAnnotation(annotations, RequestHeader.class)) != null) {
            //                parameter.setParamType(Parameter.ParamType.HEADER_PARAM);
            //                parameter.setParamName(header.name());
            //                if (parameter.getParamName().isEmpty() && !header.value().isEmpty()) {
            //                    parameter.setParamName(header.value());
            //                }
            //                if (!header.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
            //                    parameter.setDefaultValue(header.defaultValue());
            //                }
            //            } else if ((cookie = findAnnotation(annotations,
            //                    CookieValue.class)) != null) {
            //                parameter.setParamType(Parameter.ParamType.COOKIE_PARAM);
            //                parameter.setParamName(cookie.name());
            //                if (parameter.getParamName().isEmpty() &&
            //                        !cookie.value().isEmpty()) {
            //                    parameter.setParamName(cookie.value());
            //                }
            //                if (!cookie.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
            //                    parameter.setDefaultValue(cookie.defaultValue());
            //                }
            //            } else if ((uriParam = findAnnotation(annotations,
            //                    PathVariable.class)) != null) {
            //                parameter.setParamType(Parameter.ParamType.PATH_PARAM);
            //                parameter.setParamName(uriParam.name());
            //                if (parameter.getParamName().isEmpty() &&
            //                        !uriParam.value().isEmpty()) {
            //                    parameter.setParamName(uriParam.value());
            //                }
            //                parameter.setEncoded(true);
            //            } else if ((matrix = findAnnotation(annotations, MatrixVariable.class)) != null) {
            //                parameter.setParamType(Parameter.ParamType.MATRIX_PARAM);
            //                parameter.setParamName(matrix.name());
            //                if (parameter.getParamName().isEmpty() && !matrix.value().isEmpty()) {
            //                    parameter.setParamName(matrix.value());
            //                }
            //                if (!matrix.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
            //                    parameter.setDefaultValue(matrix.defaultValue());
            //                }
            //            } else if (findAnnotation(annotations,
            //                    RequestBody.class) != null) {
            //                parameter.setParamType(Parameter.ParamType.MESSAGE_BODY);
            //            } else if (parameter.getType().getName().startsWith("javax.servlet.http")) { // is this perhaps too // aggressive?
            //                parameter.setParamType(Parameter.ParamType.CONTEXT);
            //            } else {
            //                parameter.setParamType(Parameter.ParamType.UNKNOWN);
            //            }

        }
    }

}
