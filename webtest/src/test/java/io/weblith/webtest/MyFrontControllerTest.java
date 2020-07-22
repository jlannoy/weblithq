package io.weblith.webtest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class MyFrontControllerTest {

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
    
    @Test
    public void testExceptionErrorPage() {
        given()
          .when().get("/Front/exception")
          .then()
            .statusCode(500)
            .body(containsString("Error Page"))
            .body(containsString("My error message"));
    }

}