package io.weblith.webtest;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FrontControllerTest {

    @Test
    public void testHelloPage() {
        given()
          .when().get("/")
          .then()
             .statusCode(200)
                .log().body();
             //.body(containsString("Welcome"));
    }

}