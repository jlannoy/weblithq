package io.weblith.freemarker.response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.weblith.test.controllers.InjectedTemplateController;

public class InjectedTemplateControllerTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(InjectedTemplateController.class)
                    .addAsResource("templates/InjectedTemplateController/bonjour.ftlh")
                    .addAsResource("templates/directory/ola.ftlh")
                    .addAsResource(new StringAsset("quarkus.http.test-port=0"), "application.properties"));
    
    
    @Test
    public void testBonjourPage() {
        given()
          .when().get("/Front/bonjour")
          .then()
             .statusCode(200)
             .body(containsString("Bonjour World"));
    }
    
    @Test
    public void testOlaPage() {
        given()
          .when().get("/Front/ola")
          .then()
             .statusCode(200)
             .body(containsString("Ola World"));
    }

}