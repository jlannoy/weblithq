package io.weblith.core.results.writer;

import static io.restassured.config.RedirectConfig.redirectConfig;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSender;
import io.weblith.core.config.WeblithConfiguration;
import io.weblith.core.scopes.CookieBuilder;

public class RedirectControllerTest {

    private final static String CONTEXT_PATH = "/";

    private final static int SEE_OTHER = Status.SEE_OTHER.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(RedirectController.class)
                    .addAsResource("i18n/messages.properties")
                    .addAsResource(new StringAsset("quarkus.http.test-port=0\nquarkus.http.root-path=" + CONTEXT_PATH), "application.properties")
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml"));

    private RequestSender given() {
        return RestAssured.given().config(RestAssured.config().redirect(redirectConfig().followRedirects(false)));
    }

    @InjectMock
    SecurityIdentity securityIdentity;

    @Inject
    WeblithConfiguration config;

    @Test
    public void testRootRedirect() {
        given()
                .get("/Redirect/root")
                .then()
                .statusCode(SEE_OTHER)
                .header(LOCATION, is("/"));
    }

    @Test
    public void testListRedirect() {
        given()
                .get("/Redirect/simpleEntities")
                .then()
                .statusCode(SEE_OTHER)
                .header(LOCATION, is("/SimpleEntity/list"));
    }

    @Test
    public void testWithSuccessRedirect() {
        given()
                .get("/Redirect/withSuccess")
                .then()
                .statusCode(SEE_OTHER)
                .header(LOCATION, is("/"))
                .cookie(config.cookies.flashName, containsString("success"))
                .cookie(config.cookies.flashName, containsString(CookieBuilder.encode("You Win !")));
    }

    @Test
    public void testWithWarningRedirect() {
        given()
                .get("/Redirect/withWarning")
                .then()
                .statusCode(SEE_OTHER)
                .header(LOCATION, is("/"))
                .cookie(config.cookies.flashName, containsString("warning"))
                .cookie(config.cookies.flashName, containsString(CookieBuilder.encode("warning.you.win")));
    }

    @Test
    public void testWithErrorRedirect() {
        given()
                .get("/Redirect/withError")
                .then()
                .statusCode(SEE_OTHER)
                .header(LOCATION, is("/"))
                .cookie(config.cookies.flashName, containsString("error"))
                .cookie(config.cookies.flashName, containsString(CookieBuilder.encode("This is an error")));
    }

}
