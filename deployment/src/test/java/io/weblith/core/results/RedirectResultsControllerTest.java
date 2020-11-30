package io.weblith.core.results;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.hamcrest.Matchers.is;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import test.controllers.RedirectResultsController;

public class RedirectResultsControllerTest {

    private final static int NOT_ALLOWED = Status.METHOD_NOT_ALLOWED.getStatusCode();

    private final static int SEE_OTHER = Status.SEE_OTHER.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(RedirectResultsController.class)
                    .addAsResource(new StringAsset("quarkus.weblith.csrf-protected=false"), "application.properties"));

    @Test
    public void testRedirect() {
        given().config(RestAssured.config().redirect(redirectConfig().followRedirects(false)))
                .get("/RedirectResults/redirectToHome").then().statusCode(SEE_OTHER)
                .header(HttpHeaders.LOCATION, is("/"));
    }



}
