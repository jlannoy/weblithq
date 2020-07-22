package io.weblith.fomantic.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceDirectoryBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.weblith.fomantic.FomanticUIFreemarkerConfigurator;

public class WeblithFomanticUIProcessor {

    private final static String FEATURE = "fomantic-ui";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem additionalBeans() {
        return AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClasses(FomanticUIFreemarkerConfigurator.class)
                .build();
    }
    
    @BuildStep
    void nativeBuild(BuildProducer<NativeImageResourceDirectoryBuildItem> resourceDirectories,
            BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitialized) {

        resourceDirectories.produce(new NativeImageResourceDirectoryBuildItem("templates/"));

    }

}
