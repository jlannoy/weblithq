package io.weblith.core.deployment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.resteasy.spi.ResteasyDeployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.arc.processor.BuiltinScope;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceDirectoryBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.resteasy.common.spi.ResteasyJaxrsProviderBuildItem;
import io.quarkus.resteasy.server.common.deployment.ResteasyDeploymentCustomizerBuildItem;
import io.quarkus.resteasy.server.common.spi.AdditionalJaxRsResourceDefiningAnnotationBuildItem;
import io.quarkus.resteasy.server.common.spi.AdditionalJaxRsResourceMethodAnnotationsBuildItem;
import io.weblith.core.WeblithResourceBuilder;
import io.weblith.core.form.parsing.FormBodyParser;
import io.weblith.core.form.parsing.JsonBodyParser;
import io.weblith.core.form.validating.RequestContextLocaleResolver;
import io.weblith.core.logging.RequestLoggingDynamicFeature;
import io.weblith.core.logging.RequestLoggingFilter;
import io.weblith.core.parameters.date.ParametersConverterProvider;
import io.weblith.core.results.ResultResponseFilter;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.core.router.annotations.Post;
import io.weblith.core.security.AuthenticityTokenDynamicFeature;
import io.weblith.core.security.AuthenticityTokenFilter;

public class WeblithProcessor {

    private static final String FEATURE = "weblith";

    public static final DotName GET_ANNOTATION = DotName.createSimple(Get.class.getName());

    public static final DotName POST_ANNOTATION = DotName.createSimple(Post.class.getName());

    public static final DotName CONTROLLER_ANNOTATION = DotName.createSimple(Controller.class.getName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public AdditionalJaxRsResourceDefiningAnnotationBuildItem registerControllersAsResources() {
        return new AdditionalJaxRsResourceDefiningAnnotationBuildItem(CONTROLLER_ANNOTATION);
    }

    @BuildStep
    public BeanDefiningAnnotationBuildItem registerControllersAsBeans() {
        return new BeanDefiningAnnotationBuildItem(CONTROLLER_ANNOTATION, BuiltinScope.SINGLETON.getName());
    }

    @BuildStep
    public AdditionalJaxRsResourceMethodAnnotationsBuildItem registerGetAndPostAsResourceMethods() {
        return new AdditionalJaxRsResourceMethodAnnotationsBuildItem(Arrays.asList(GET_ANNOTATION, POST_ANNOTATION));
    }

    @BuildStep
    public void registerJaxrsProviders(BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        providers.produce(new ResteasyJaxrsProviderBuildItem(FormBodyParser.class.getName()));
        providers.produce(new ResteasyJaxrsProviderBuildItem(JsonBodyParser.class.getName()));

        providers.produce(new ResteasyJaxrsProviderBuildItem(ResultResponseFilter.class.getName()));
        providers.produce(new ResteasyJaxrsProviderBuildItem(AuthenticityTokenDynamicFeature.class.getName()));
        providers.produce(new ResteasyJaxrsProviderBuildItem(RequestLoggingDynamicFeature.class.getName()));

        providers.produce(new ResteasyJaxrsProviderBuildItem(ParametersConverterProvider.class.getName()));
    }

    @BuildStep
    public void registerWeblithResourceBuilderForControllers(BeanArchiveIndexBuildItem beanArchiveIndexBuildItem,
            BuildProducer<ResteasyDeploymentCustomizerBuildItem> deploymentCustomizerProducer,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {

        validateControllers(beanArchiveIndexBuildItem);

        final Collection<AnnotationInstance> annotations = beanArchiveIndexBuildItem.getIndex()
                .getAnnotations(CONTROLLER_ANNOTATION);
        if (annotations.isEmpty()) {
            return;
        }

        final Set<String> classNames = new HashSet<>();
        for (AnnotationInstance annotation : annotations) {
            classNames.add(annotation.target().asClass().toString());
        }

        deploymentCustomizerProducer
                .produce(new ResteasyDeploymentCustomizerBuildItem(new Consumer<ResteasyDeployment>() {
                    @Override
                    public void accept(ResteasyDeployment resteasyDeployment) {
                        resteasyDeployment.getScannedResourceClassesWithBuilder()
                                .put(WeblithResourceBuilder.class.getName(), new ArrayList<>(classNames));
                    }
                }));

        reflectiveClass
                .produce(new ReflectiveClassBuildItem(true, false, false, WeblithResourceBuilder.class.getName()));

    }

    @BuildStep
    public AdditionalBeanBuildItem additionalBeans() {

        return AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClasses(AuthenticityTokenFilter.class)
                .addBeanClasses(RequestLoggingFilter.class)
                .addBeanClasses(RequestContextLocaleResolver.class)
                .build();

    }

    /**
     * Make sure the controllers have the proper annotation and warn if not
     */
    private void validateControllers(BeanArchiveIndexBuildItem beanArchiveIndexBuildItem) {
        IndexView index = beanArchiveIndexBuildItem.getIndex();

        for (DotName annotationName : Arrays.asList(GET_ANNOTATION, POST_ANNOTATION)) {
            Collection<AnnotationInstance> annotations = index.getAnnotations(annotationName);

            for (AnnotationInstance annotation : annotations) {
                MethodInfo methodInfo = annotation.target().asMethod();
                // TODO how to test
                //                if (methodInfo.hasAnnotation(GET_ANNOTATION) && methodInfo.hasAnnotation(POST_ANNOTATION)) {
                //                    throw new IllegalStateException(
                //                            "Method " + methodInfo.name() + " in " + methodInfo.declaringClass().simpleName()
                //                                    + " cannot have @POST and @GET annotation at the same time");
                //                }
                //                if (methodInfo.declaringClass().classAnnotation(CONTROLLER_ANNOTATION) == null) {
                //                    throw new IllegalStateException(
                //                            "Method " + methodInfo.name() + " in " + methodInfo.declaringClass().simpleName() + " have a @"
                //                                    + annotationName.withoutPackagePrefix()
                //                                    + " annotation must have @Controller on it's declaring class");
                //                }
            }

        }

        Collection<AnnotationInstance> controllerAnnotations = index.getAnnotations(CONTROLLER_ANNOTATION);
        nextController: for (AnnotationInstance annotation : controllerAnnotations) {
            for (MethodInfo method : annotation.target().asClass().methods()) {
                if (method.hasAnnotation(GET_ANNOTATION) || method.hasAnnotation(POST_ANNOTATION)) {
                    continue nextController;
                }
            }
            //            throw new IllegalStateException(
            //                    "Controller " + annotation.target().asClass().name() + " have no @POST or @GET route");
        }

    }

    //    @BuildStep
    //    @Record(ExecutionTime.RUNTIME_INIT)
    //    public void staticAssetsManagement(BuildProducer<RouteBuildItem> routes, StaticAssetsHandlerRecorder recorder) {
    //        routes.produce(new RouteBuildItem("/assets/lib/*", recorder.handler("META-INF/resources/webjars"), false));
    //    }

    @BuildStep
    public void nativeBuild(BuildProducer<NativeImageResourceDirectoryBuildItem> resourceDirectories,
            BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitialized) {

    }
}
