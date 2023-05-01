package io.weblith.freemarker.deployment;

import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

import freemarker.ext.jython.JythonModel;
import freemarker.ext.jython.JythonWrapper;
import freemarker.template.Template;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
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
import io.weblith.freemarker.response.HtmlResult;
import io.weblith.freemarker.response.HtmlResultBodyWriter;
import io.weblith.freemarker.template.TemplateResolver;

public class WeblithFreemarkerProcessor {

    private final static String FEATURE = "weblith-freemarker";

    public static final DotName TEMPLATE = DotName.createSimple(Template.class.getCanonicalName());

    public static final DotName HTML_RESULT = DotName.createSimple(HtmlResult.class.getCanonicalName());

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
                        TemplateResolver.class)
                .build();
    }

    private String getFieldOrParameterName(InjectionPointInfo injectionPoint) {
        if (injectionPoint.isField()) {
            return injectionPoint.getTarget().asField().name();
        } else if (injectionPoint.isParam()) {
            String name = injectionPoint.getTarget().asMethod().parameterName(injectionPoint.getPosition());
            return name == null ? injectionPoint.getTarget().asMethod().name() : name;
        }
        throw new IllegalArgumentException("Cannot obtain name for an other injection point for " + injectionPoint.getTarget().kind());
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
