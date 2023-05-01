package io.weblith.core.i18n;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.junit.QuarkusTest;
import io.weblith.core.config.WeblithConfig;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;
import test.controllers.RequestContextAwareController;

@QuarkusTest
public class MultipleLocalesControllerTest {

    private final static int OK = Status.OK.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(RequestContextAwareController.class)
                    .addAsResource("application.properties"));

    @Inject
    WeblithConfig config;

    @Test
    public void testGetDefaultLocale() {
        when().get("/Ctx/locale")
                .then().statusCode(OK).body(is("de"));
    }

    @Test
    public void testGetHeaderLocale() {
        given().header("Accept-Language", "en")
                .when().get("/Ctx/locale")
                .then().statusCode(OK).body(is("en"));
    }

    @Test
    public void testGetQueryParamLocale() {
        given().queryParam(config.switchLanguageParam, "fr")
                .when().get("/Ctx/locale")
                .then().statusCode(OK).body(is("fr"));
    }
}
