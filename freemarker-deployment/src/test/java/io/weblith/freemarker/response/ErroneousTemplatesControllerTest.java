package io.weblith.freemarker.response;

import io.quarkus.test.QuarkusUnitTest;
import io.weblith.core.logging.NotLogged;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import io.weblith.freemarker.response.exceptions.AnyExceptionMapper;
import io.weblith.freemarker.response.exceptions.WebApplicationExceptionMapper;
import io.weblith.freemarker.template.FreemarkerTemplate;
import io.weblith.freemarker.template.TemplatePath;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

public class ErroneousTemplatesControllerTest {

    @Controller("/MissingTemplate")
    @NotLogged
    public static class MissingTemplateController {

        @Get
        public HtmlResult error() {
            return new HtmlResult("errors","missing");
        }

    }

    @Controller("/NotClosedTag")
    @NotLogged
    public static class NotClosedTagTemplateController {

        @Get
        public HtmlResult error() {
            return new HtmlResult("errors","notClosedTag");
        }

    }

    @Controller("/NotExistingTag")
    @NotLogged
    public static class NotExistingTagTemplateController {

        @Get
        public HtmlResult error() {
            return new HtmlResult("errors","notExistingTag");
        }

    }

    @Controller("/EmptyVariable")
    @NotLogged
    public static class EmptyVariableTemplateController {

        @Get
        public HtmlResult error() {
            return new HtmlResult("errors","emptyVariable");
        }

    }

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(MissingTemplateController.class)
                    .addClass(NotClosedTagTemplateController.class)
                    .addAsResource("templates/errors/notClosedTag.ftlh")
                    .addClass(NotExistingTagTemplateController.class)
                    .addAsResource("templates/errors/notExistingTag.ftlh")
                    .addClass(EmptyVariableTemplateController.class)
                    .addAsResource("templates/errors/emptyVariable.ftlh")
                    .addClass(WebApplicationExceptionMapper.class)
                    .addAsResource("application.properties"));

    @Test
    public void testMissingTemplate() {
        given()
                .when().get("/MissingTemplate/error")
                .then()
                // .statusCode(500)
                .body(containsString("Error injecting"));
    }

    @Test
    public void testNotClosedTag() {
        given()
                .when().get("/NotClosedTag/error")
                .then()
                // .statusCode(500)
                .body(containsString("Error injecting"));
    }

    @Test
    public void testNotExistingTag() {
        given()
                .when().get("/NotExistingTag/error")
                .then()
                .statusCode(500)
                .body(containsString("Error injecting"));
    }

    @Test
    public void testEmptyVariable() {
        given()
                .when().get("/EmptyVariable/error")
                .then()
                // .statusCode(500)
                .body(containsString("Error injecting"));
    }
}