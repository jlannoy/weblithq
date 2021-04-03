package io.weblith.core.multitenancy;

import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import test.controllers.MultiTenantController;
import test.controllers.MySecondController;

import javax.ws.rs.core.Response.Status;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

public class TenantResolverFilterTest {

    private final static int OK = Status.OK.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(MultiTenantController.class)
                    .addAsResource(new StringAsset("quarkus.weblith.tenant.domain.test=localhost\nquarkus.weblith.tenant.domain.test2=127.0.0.1\nquarkus.http.test-port=0"), "application.properties"));

    @Test
    public void testTenantId() {
        when().get("/Tenant/id").then().statusCode(OK).body(is("test"));
    }

    @Test
    public void testTenantDomain() {
        when().get("/Tenant/domain").then().statusCode(OK).body(is("localhost"));
    }

}
