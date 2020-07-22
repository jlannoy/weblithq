package io.weblith.freemarker.response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ErroneousTemplateControllerTest {

    @Test
    public void testHelloPage() {
        given()
          .when().get("/TemplateError/hello")
          .then()
             .statusCode(500)
             .body(containsString("FreemarkerRenderingException"));
    }

}