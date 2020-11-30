package io.weblith.core.router;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import test.controllers.MethodParametersController;

public class MethodParametersControllerTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(MethodParametersController.class)
                    .addAsResource(new StringAsset("quarkus.weblith.csrf-protected=false"), "application.properties"));

    @Test
    public void testPathParam() {
        when().get("/params/pathParam/my-param").then().body(is("get my-param"));
    }

    @Test
    public void testNamedPathParam() {
        when().get("/params/named/pathParam/my-param").then().body(is("get my-param"));
    }
    
    @Test
    public void testQueryParam() {
        when().get("/params/queryParam?value=my-value").then().body(is("get my-value"));
        when().get("/params/queryParam?value=").then().body(is("get "));
        when().get("/params/queryParam").then().body(is("get null"));
    }
    
    @Test
    public void testNamedQueryParam() {
        when().get("/params/named/queryParam?value=my-value").then().body(is("get my-value"));
        when().get("/params/named/queryParam?value=").then().body(is("get "));
        when().get("/params/named/queryParam").then().body(is("get null"));
    }


    @Test
    public void testOptionalQueryParam() {
        when().get("/params/optionalQueryParam?value=my-opt-value").then().body(is("get my-opt-value"));
        when().get("/params/optionalQueryParam?value=").then().body(is("get "));
        when().get("/params/optionalQueryParam").then().body(is("get default-value"));
    }
    
    @Test
    public void testNamedOptionalQueryParam() {
        when().get("/params/named/optionalQueryParam?value=my-opt-value").then().body(is("get my-opt-value"));
        when().get("/params/named/optionalQueryParam?value=").then().body(is("get "));
        when().get("/params/named/optionalQueryParam").then().body(is("get default-value"));
    }

}
