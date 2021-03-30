package io.weblith.core.results;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static test.controllers.StreamResultsController.MY_TXT_FILE;
import static test.controllers.StreamResultsController.MY_UNKNOWN_TYPE_FILE;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.http.client.utils.DateUtils;
import org.jboss.resteasy.core.Headers;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import test.controllers.StreamResultsController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class StreamResultsControllerTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
                                                         .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                                                                                             .addClasses(StreamResultsController.class)
                                                                                             .addAsResource(MY_UNKNOWN_TYPE_FILE)
                                                                                             .addAsResource(MY_TXT_FILE)
                                                                                             .addAsResource(new StringAsset("quarkus.http.test-port=0"), "application.properties"));

    @Test
    public void testFileStreamResult() {
        when()
              .get("/StreamResults/file")
              .then()
              .statusCode(200)
              .contentType(containsString(MediaType.TEXT_PLAIN))
              .body(is("file content"));
    }

    @Test
    public void testUrlStreamResult() {
        when()
              .get("/StreamResults/url")
              .then()
              .statusCode(200)
              .contentType(containsString(MediaType.TEXT_PLAIN))
              .body(is("file content"));
    }

    @Test
    public void testUnknownTypeFileStreamResult() {
        when()
              .get("/StreamResults/unknownTypeFile")
              .then()
              .statusCode(200)
              .contentType(containsString(MediaType.APPLICATION_OCTET_STREAM))
              .body(is("file content 2"));
    }

    @Test
    public void testUnknownTypeUrlStreamResult() {
        when()
              .get("/StreamResults/unknownTypeUrl")
              .then()
              .statusCode(200)
              .contentType(containsString(MediaType.APPLICATION_OCTET_STREAM))
              .body(is("file content 2"));
    }

    @Test
    public void testNotModifiedFileStreamResult() {
        given()
            .header(HttpHeaders.IF_MODIFIED_SINCE, DateUtils.formatDate(new Date(), DateUtils.PATTERN_RFC1123))
        .when()
              .get("/StreamResults/file")
              .then()
              .statusCode(Status.NOT_MODIFIED.getStatusCode())
              .contentType(containsString(MediaType.TEXT_PLAIN))
              .body(is(emptyOrNullString()));
    }
}
