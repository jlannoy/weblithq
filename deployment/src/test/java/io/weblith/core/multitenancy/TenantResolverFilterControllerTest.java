package io.weblith.core.multitenancy;

import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import test.controllers.MultiTenantController;

import javax.ws.rs.core.Response.Status;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class TenantResolverFilterControllerTest {

    private final static int OK = Status.OK.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(MultiTenantController.class)
                    .addAsResource("application.properties"));

    @Test
    public void testTenantId() {
        given().baseUri("http://localhost")
                .when().get("/Tenant/id")
                .then().statusCode(OK).body(is("test"));
        given().baseUri("http://127.0.0.1")
                .when().get("/Tenant/id")
                .then().statusCode(OK).body(is("test2"));
    }

    @Test
    public void testTenantDomain() {
        given().baseUri("http://localhost")
                .when().get("/Tenant/domain")
                .then().statusCode(OK).body(is("localhost"));
        given().baseUri("http://127.0.0.1")
                .when().get("/Tenant/domain")
                .then().statusCode(OK).body(is("127.0.0.1"));
    }

}
