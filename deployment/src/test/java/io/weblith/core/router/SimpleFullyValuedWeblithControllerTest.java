package io.weblith.core.router;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

import javax.ws.rs.core.Response.Status;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import test.controllers.SimpleFullyValuedWeblithController;

public class SimpleFullyValuedWeblithControllerTest {

    private final static int NOT_ALLOWED = Status.METHOD_NOT_ALLOWED.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(SimpleFullyValuedWeblithController.class)
                    // .addAsResource("application.properties")
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                    .addAsResource(new StringAsset("quarkus.http.test-port=8888"), "application.properties"));

    @Test
    public void testGet() {
        when().get("/weblith/get").then().body(is("get weblith"));
        when().post("/weblith/get").then().statusCode(NOT_ALLOWED);
    }

    @Test
    public void testPost() {
        when().post("/weblith/post").then().body(is("post weblith"));
        when().get("/weblith/post").then().statusCode(NOT_ALLOWED);
    }

    @Test
    public void testPathParam() {
        when().get("/weblith/pathParam/my-param").then().body(is("get my-param"));
    }

    @Test
    public void testQueryParam() {
        when().get("/weblith/queryParam?value=my-value").then().body(is("get my-value"));
        when().get("/weblith/queryParam?value=").then().body(is("get "));
        when().get("/weblith/queryParam").then().body(is("get null"));
    }

    @Test
    public void testOptionalQueryParam() {
        when().get("/weblith/optionalQueryParam?value=my-opt-value").then().body(is("get my-opt-value"));
        when().get("/weblith/optionalQueryParam?value=").then().body(is("get "));
        when().get("/weblith/optionalQueryParam").then().body(is("get default-value"));
    }
}
