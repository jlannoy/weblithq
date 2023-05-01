package io.weblith.core.router.errors;

import static io.restassured.RestAssured.when;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.junit.QuarkusTest;
import io.weblith.core.router.annotations.Get;
import jakarta.ws.rs.core.Response.Status;

@QuarkusTest
public class MissingAnnotationControllerTest {

    private final static int NOT_FOUND = Status.NOT_FOUND.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(MissingAnnotationWeblithController.class)
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                    .addAsResource(new StringAsset("quarkus.http.test-port=0"), "application.properties"));

    public static class MissingAnnotationWeblithController {

        @Get("/ignored")
        public String get() {
            return "ignored";
        }

    }

    @Test
    public void testWeblithIgnoredResource() {
        when().get("/ignored").then().statusCode(NOT_FOUND);
        when().get("/MissingAnnotationWeblith/ignored").then().statusCode(NOT_FOUND);
        when().get("/MissingAnnotationWeblithController/ignored").then().statusCode(NOT_FOUND);
    }

}
