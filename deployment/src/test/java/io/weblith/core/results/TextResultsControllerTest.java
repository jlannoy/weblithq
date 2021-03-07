package io.weblith.core.results;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import test.controllers.TextResultsController;

public class TextResultsControllerTest {

    private final static int OK = Status.OK.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest().setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                                                                                             .addClasses(TextResultsController.class)
                                                                                             .addAsResource(new StringAsset("quarkus.weblith.csrf-protected=false"), "application.properties"));

    @Test
    public void testSimpleTextResult() {
        when().get("/TextResults/getSimpleTextResult")
              .then()
              .statusCode(OK)
              .body(is("Hello there!"))
              .contentType(containsString(MediaType.TEXT_PLAIN));
    }

    @Test
    public void testTextResultFromObject() {
        when().get("/TextResults/getTextResultFromObject")
              .then()
              .statusCode(Status.ACCEPTED.getStatusCode())
              .body(is("[Hello, there, !]"))
              .header(HttpHeaders.CONTENT_TYPE, is("text/javascript; charset=US-ASCII"));
    }

    @Test
    public void testTextResultFromException() {
        when().get("/TextResults/getTextResultFromException")
              .then()
              .statusCode(OK)
              .body(is("No hello there..."));
    }

}
