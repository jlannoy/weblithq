package io.weblith.webtest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

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