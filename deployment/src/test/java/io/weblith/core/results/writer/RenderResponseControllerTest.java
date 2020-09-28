package io.weblith.core.results.writer;

import static io.restassured.RestAssured.when;
import static io.weblith.core.results.writer.RenderResponseController.MY_TXT_FILE;
import static io.weblith.core.results.writer.RenderResponseController.MY_UNKNOWN_TYPE_FILE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import javax.ws.rs.core.MediaType;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class RenderResponseControllerTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(RenderResponseController.class)
                    .addAsResource(MY_UNKNOWN_TYPE_FILE)
                    .addAsResource(MY_TXT_FILE)
                    .addAsResource(new StringAsset("quarkus.http.test-port=0"), "application.properties"));

    @Test
    public void testTextResult() {
        when()
                .get("/RenderResponse/text")
                .then()
                .statusCode(200)
                // TODO check .contentType(containsString(MediaType.TEXT_PLAIN))
                .body(is("hello weblith"));
    }

    @Test
    public void testFileStreamResult() {
        when()
                .get("/RenderResponse/file")
                .then()
                .statusCode(200)
                .contentType(containsString(MediaType.TEXT_PLAIN))
                .body(is("file content"));
    }

    @Test
    public void testUrlStreamResult() {
        when()
                .get("/RenderResponse/url")
                .then()
                .statusCode(200)
                .contentType(containsString(MediaType.TEXT_PLAIN))
                .body(is("file content"));
    }

    @Test
    public void testUnknownTypeFileStreamResult() {
        when()
                .get("/RenderResponse/unknownTypeFile")
                .then()
                .statusCode(200)
                .contentType(containsString(MediaType.APPLICATION_OCTET_STREAM))
                .body(is("file content 2"));
    }

    @Test
    public void testUnknownTypeUrlStreamResult() {
        when()
                .get("/RenderResponse/unknownTypeUrl")
                .then()
                .statusCode(200)
                .contentType(containsString(MediaType.APPLICATION_OCTET_STREAM))
                .body(is("file content 2"));
    }

}
