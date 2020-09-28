package io.weblith.freemarker.response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import java.util.UUID;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.weblith.test.controllers.MyFrontEndController;

public class MyFrontEndControllerTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(MyFrontEndController.class)
                    .addAsResource("templates/MyFrontEndController/hello.ftlh")
                    .addAsResource(new StringAsset("quarkus.http.test-port=8888"), "application.properties"));
    
    @Test
    public void testHelloPage() {
        given()
          .when().get("/Front/hello")
          .then()
             .statusCode(200)
             .body(containsString("Hello World"));
    }

    @Test
    public void testHelloCustomizedPage() {
        String uuid = UUID.randomUUID().toString();
        given()
          .queryParam("name", uuid)
          .when().get("/Front/hello")
          .then()
            .statusCode(200)
            .body(containsString("Hello " + uuid));
    }
    
//    @Test
//    public void testExceptionErrorPage() {
//        given()
//          .when().get("/Front/exception")
//          .then()
//            .statusCode(500)
//            .body(containsString("Error Page"))
//            .body(containsString("My error message"));
//    }

}