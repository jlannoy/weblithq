package io.weblith.core.scopes;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;
import test.controllers.RequestContextAwareController;

@QuarkusTest
public class RequestContextAwareControllerTest {

    private final static int OK = Status.OK.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(RequestContextAwareController.class)
                    .addAsResource(new StringAsset("quarkus.http.test-port=0"), "application.properties"));

    @Test
    public void testAuthenticityTokenExist() {
        // Will also checks the session exist
        when().get("/Ctx/authenticityToken")
                .then().statusCode(OK).body(not(emptyOrNullString()));
    }

    @Test
    public void testControllerName() {
        // Will also checks the session exist
        when().get("/Ctx/controllerName")
                .then().statusCode(OK).body(is("RequestContextAwareController"));
    }
}
