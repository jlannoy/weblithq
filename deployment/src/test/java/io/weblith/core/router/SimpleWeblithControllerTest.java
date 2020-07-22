package io.weblith.core.router;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.hamcrest.Matchers.is;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import test.controllers.SimpleWeblithController;

public class SimpleWeblithControllerTest {

    private final static int NOT_ALLOWED = Status.METHOD_NOT_ALLOWED.getStatusCode();

    private final static int SEE_OTHER = Status.SEE_OTHER.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(SimpleWeblithController.class)
                    // .addAsResource("application.properties")
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                    .addAsResource(new StringAsset("quarkus.http.test-port=8888"), "application.properties"));

    @Test
    public void testGet() {
        when().post("/SimpleWeblith/get").then().statusCode(NOT_ALLOWED);
    }

    @Test
    public void testPost() {
        when().post("/SimpleWeblith/post").then().body(is("post weblith"));
        when().get("/SimpleWeblith/post").then().statusCode(NOT_ALLOWED);
    }

    @Test
    public void testPathParam() {
        when().get("/SimpleWeblith/pathParam/my-param").then().body(is("get my-param"));
    }

    @Test
    public void testQueryParam() {
        when().get("/SimpleWeblith/queryParam?value=my-value").then().body(is("get my-value"));
        when().get("/SimpleWeblith/queryParam?value=").then().body(is("get "));
        when().get("/SimpleWeblith/queryParam").then().body(is("get null"));
    }

    @Test
    public void testOptionalQueryParam() {
        when().get("/SimpleWeblith/optionalQueryParam?value=my-opt-value").then().body(is("get my-opt-value"));
        when().get("/SimpleWeblith/optionalQueryParam?value=").then().body(is("get "));
        when().get("/SimpleWeblith/optionalQueryParam").then().body(is("get default-value"));
    }

    @Test
    public void testRedirect() {
        given().config(RestAssured.config().redirect(redirectConfig().followRedirects(false)))
                .get("/SimpleWeblith/redirect").then().statusCode(SEE_OTHER)
                .header(HttpHeaders.LOCATION, is("/"));
    }
}
