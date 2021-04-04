package io.weblith.core.multitenancy;

import io.quarkus.test.QuarkusUnitTest;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class TenantScopedTest {

    private final static int OK = Response.Status.OK.getStatusCode();

    private final static int ACCEPTED = Response.Status.ACCEPTED.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TenantResolverFilter.class)
                    .addClasses(TenantResolverFilterDynamicFeature.class)
                    .addClasses(MultiTenantController.class)
                    .addClasses(TenantScopedService.class)
                    .addAsResource("application.properties"));

    @Test
    public void testTenantScopedService() {
        given().baseUri("http://localhost")
                .when().get("/Tenant/setValue/IAmSexy")
                .then().statusCode(ACCEPTED);
        given().baseUri("http://127.0.0.1")
                .when().get("/Tenant/setValue/AndIKnowIt")
                .then().statusCode(ACCEPTED);
        given().baseUri("http://localhost")
                .when().get("/Tenant/getValue")
                .then().statusCode(OK)
                .body(is("IAmSexy"));
        given().baseUri("http://127.0.0.1")
                .when().get("/Tenant/getValue")
                .then().statusCode(OK)
                .body(is("AndIKnowIt"));
    }

    @TenantScoped
    public static class TenantScopedService {

        private String value;

        public void set(String value) {
            this.value = value;
        }

        public String get() {
            return this.value;
        }

    }

    @Controller("/Tenant")
    public static class MultiTenantController {

        @Inject
        TenantScopedService service;

        @Get
        public Response setValue(@PathParam String value) {
            service.set(value);
            return Response.accepted().build();
        }

        @Get
        public Response getValue() {
            return Response.ok().entity(service.get()).build();
        }
    }
}
