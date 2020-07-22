#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.webtest.domains.simpleEntity;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SimpleEntityControllerTest {

    @Test
    public void testListPage() {
        given()
          .when()
              .get("/SimpleEntity/list")
          .then()
              .statusCode(200)
              .body(containsString("SimpleEntity list"))
              .body(containsString("Total : 3 x SimpleEntity"));
    }

    @Test
    public void testSave() {
        given()
          .when()
              // .contentType(ContentType.URLENC)
              .formParam("name", "toto")
              .formParam("type", "D")
              // .body(new SimpleEntityForm())
              .post("/SimpleEntity/save")
          .then()
              .statusCode(200)
              .body(containsString("SimpleEntity list"));
    }
}