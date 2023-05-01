package io.weblith.core.results;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static jakarta.ws.rs.core.HttpHeaders.LOCATION;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.weblith.core.config.WeblithConfig;
import io.weblith.core.scopes.CookieBuilder;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response.Status;
import test.controllers.RedirectResultsController;

@QuarkusTest
public class RedirectResultsControllerTest {

    private final static int SEE_OTHER = Status.SEE_OTHER.getStatusCode();

    private final static int NOT_MODIFIED = Status.NOT_MODIFIED.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(RedirectResultsController.class)
                    .addAsResource("i18n/messages.properties")
                    .addAsResource("application.properties"));
    @Inject
    WeblithConfig config;

    @Test
    public void testSimpleRedirect() {
        given().config(RestAssured.config()
                .redirect(redirectConfig().followRedirects(false)))
                .get("/RedirectResults/redirectToHome")
                .then()
                .statusCode(SEE_OTHER)
                .header(HttpHeaders.LOCATION, is("/"));
    }

    @Test
    public void testNotModifiedRedirect() {
        given().config(RestAssured.config()
                .redirect(redirectConfig().followRedirects(false)))
                .get("/RedirectResults/simpleEntities")
                .then()
                .statusCode(NOT_MODIFIED)
                .header(LOCATION, is("/SimpleEntity/list"));
    }

    @Test
    public void testWithSuccessRedirect() {
        given().config(RestAssured.config()
                .redirect(redirectConfig().followRedirects(false)))
                .post("/RedirectResults/withSuccess")
                .then()
                .statusCode(SEE_OTHER)
                .header(LOCATION, is("/"))
                .cookie(config.flash.cookieName, containsString("success"))
                .cookie(config.flash.cookieName, containsString(CookieBuilder.encode("You Win !")));
    }

    @Test
    public void testWithWarningRedirect() {
        given().config(RestAssured.config()
                .redirect(redirectConfig().followRedirects(false)))
                .post("/RedirectResults/withWarning")
                .then()
                .statusCode(SEE_OTHER)
                .header(LOCATION, is("/"))
                .cookie(config.flash.cookieName, containsString("warning"))
                .cookie(config.flash.cookieName, containsString(CookieBuilder.encode("warning.you.lose")));
    }

    @Test
    public void testWithErrorRedirect() {
        given().config(RestAssured.config()
                .redirect(redirectConfig().followRedirects(false)))
                .post("/RedirectResults/withError")
                .then()
                .statusCode(SEE_OTHER)
                .header(LOCATION, is("/"))
                .cookie(config.flash.cookieName, containsString("error"))
                .cookie(config.flash.cookieName, containsString(CookieBuilder.encode("This is an error")));
    }

}
