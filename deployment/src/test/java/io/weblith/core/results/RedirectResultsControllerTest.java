package io.weblith.core.results;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import io.weblith.core.config.WeblithConfig;
import io.weblith.core.scopes.CookieBuilder;
import test.controllers.RedirectResultsController;

public class RedirectResultsControllerTest {

    private final static int SEE_OTHER = Status.SEE_OTHER.getStatusCode();

    private final static int NOT_MODIFIED = Status.NOT_MODIFIED.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
                                                         .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                                                                                             .addClasses(RedirectResultsController.class)
                                                                                             .addAsResource("i18n/messages.properties")
                                                                                             .addAsResource(new StringAsset("quarkus.weblith.csrf-protected=false"), "application.properties"));
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
               .cookie(config.cookies.flashName, containsString("success"))
               .cookie(config.cookies.flashName, containsString(CookieBuilder.encode("You Win !")));
    }

    @Test
    public void testWithWarningRedirect() {
        given().config(RestAssured.config()
                                  .redirect(redirectConfig().followRedirects(false)))
               .post("/RedirectResults/withWarning")
               .then()
               .statusCode(SEE_OTHER)
               .header(LOCATION, is("/"))
               .cookie(config.cookies.flashName, containsString("warning"))
               .cookie(config.cookies.flashName, containsString(CookieBuilder.encode("warning.you.lose")));
    }

    @Test
    public void testWithErrorRedirect() {
        given().config(RestAssured.config()
                                  .redirect(redirectConfig().followRedirects(false)))
               .post("/RedirectResults/withError")
               .then()
               .statusCode(SEE_OTHER)
               .header(LOCATION, is("/"))
               .cookie(config.cookies.flashName, containsString("error"))
               .cookie(config.cookies.flashName, containsString(CookieBuilder.encode("This is an error")));
    }

}
