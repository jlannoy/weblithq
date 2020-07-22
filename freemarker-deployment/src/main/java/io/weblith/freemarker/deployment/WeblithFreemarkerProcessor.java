package io.weblith.freemarker.deployment;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

import freemarker.ext.jython.JythonModel;
import freemarker.ext.jython.JythonWrapper;
import freemarker.template.Template;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem;
import io.quarkus.arc.processor.BuildExtension;
import io.quarkus.arc.processor.InjectionPointInfo;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceDirectoryBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyIgnoreWarningBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.resteasy.common.spi.ResteasyJaxrsProviderBuildItem;
import io.weblith.freemarker.FreemarkerConfigurationProvider;
import io.weblith.freemarker.config.FreemarkerConfig;
import io.weblith.freemarker.response.HtmlResult;
import io.weblith.freemarker.response.HtmlResultBodyWriter;
import io.weblith.freemarker.response.exceptions.AnyExceptionMapper;
import io.weblith.freemarker.response.exceptions.WebApplicationExceptionMapper;
import io.weblith.freemarker.template.FreemarkerTemplate;
import io.weblith.freemarker.template.FreemarkerTemplateProducer;
import io.weblith.freemarker.template.TemplatePath;
import io.weblith.freemarker.template.TemplateResolver;

public class WeblithFreemarkerProcessor {

    private final static String FEATURE = "freemarker";

    public static final DotName TEMPLATE = DotName.createSimple(Template.class.getCanonicalName());

    public static final DotName FREEMARKER_TEMPLATE = DotName.createSimple(FreemarkerTemplate.class.getCanonicalName());

    public static final DotName HTML_RESULT = DotName.createSimple(HtmlResult.class.getCanonicalName());

    public static final DotName TEMPLATE_PATH = DotName.createSimple(TemplatePath.class.getCanonicalName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void jaxrsProviders(BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        providers.produce(new ResteasyJaxrsProviderBuildItem(HtmlResultBodyWriter.class.getCanonicalName()));
        // providers.produce(new ResteasyJaxrsProviderBuildItem(WebApplicationExceptionMapper.class.getCanonicalName()));
        // providers.produce(new ResteasyJaxrsProviderBuildItem(AnyExceptionMapper.class.getCanonicalName()));
    }

    @BuildStep
    AdditionalBeanBuildItem additionalBeans() {
        return AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClasses(FreemarkerConfigurationProvider.class,
                        TemplateResolver.class,
                        FreemarkerTemplateProducer.class,
                        TemplatePath.class)
                .build();
    }

    @BuildStep
    void validateTemplateInjectionPoints(FreemarkerConfig config,
            ValidationPhaseBuildItem validationPhase,
            BuildProducer<ValidationErrorBuildItem> validationErrors) {

        for (InjectionPointInfo injectionPoint : validationPhase.getContext().get(BuildExtension.Key.INJECTION_POINTS)) {

            if (injectionPoint.getRequiredType().name().equals(FREEMARKER_TEMPLATE)) {

                AnnotationInstance resourcePath = injectionPoint.getRequiredQualifier(TEMPLATE_PATH);
                String path;
                if (resourcePath != null) {
                    path = resourcePath.value().asString();
                } else if (injectionPoint.hasDefaultedQualifier()) {
                    path = getName(injectionPoint);
                } else {
                    path = null;
                }
                if (path != null) {
                    // TODO check template can be found
                    // validationErrors.produce(new ValidationErrorBuildItem(
                    //         new IllegalStateException("No template found for " + injectionPoint.getTargetInfo())));
                }
            }
        }
    }

    public static String getName(InjectionPointInfo injectionPoint) {
        if (injectionPoint.isField()) {
            return injectionPoint.getTarget().asField().name();
        } else if (injectionPoint.isParam()) {
            String name = injectionPoint.getTarget().asMethod().parameterName(injectionPoint.getPosition());
            return name == null ? injectionPoint.getTarget().asMethod().name() : name;
        }
        throw new IllegalArgumentException();
    }

    @BuildStep
    void nativeBuild(BuildProducer<NativeImageResourceDirectoryBuildItem> resourceDirectories,
            BuildProducer<NativeImageResourceBuildItem> resources,
            BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitialized) {

        resourceDirectories.produce(new NativeImageResourceDirectoryBuildItem("templates/"));

        resources.produce(new NativeImageResourceBuildItem("freemarker/version.properties"));

        runtimeInitialized.produce(new RuntimeInitializedClassBuildItem(JythonWrapper.class.getName()));
        runtimeInitialized.produce(new RuntimeInitializedClassBuildItem(JythonModel.class.getName()));

    }

    @BuildStep
    void registerForReflection(BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchy,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<ReflectiveHierarchyIgnoreWarningBuildItem> ignoreWarnings) {

        reflectiveHierarchy.produce(
                new ReflectiveHierarchyBuildItem(Type.create(FREEMARKER_TEMPLATE, Type.Kind.CLASS)));
        reflectiveHierarchy.produce(
                new ReflectiveHierarchyBuildItem(Type.create(HTML_RESULT, Type.Kind.CLASS)));

        ignoreWarnings.produce(
                new ReflectiveHierarchyIgnoreWarningBuildItem(
                        new ReflectiveHierarchyIgnoreWarningBuildItem.DotNameExclusion(TEMPLATE)));

    }

    // TODO Adds a lot of warnings
    //    @BuildStep
    //    void addDependencies(BuildProducer<IndexDependencyBuildItem> indexDependency) {
    //        indexDependency.produce(new IndexDependencyBuildItem("org.freemarker", "freemarker"));
    //    }
}
