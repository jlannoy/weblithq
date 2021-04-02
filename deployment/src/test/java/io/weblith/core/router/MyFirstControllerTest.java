package io.weblith.core.router;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

import javax.ws.rs.core.Response.Status;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import test.controllers.MyFirstController;

public class MyFirstControllerTest {

    private final static int OK = Status.OK.getStatusCode();

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(MyFirstController.class)
                    .addAsResource(new StringAsset("quarkus.weblith.csrf-protected=false\nquarkus.http.test-port=0"), "application.properties"));

    @Test
    public void testMyPage() {
        when().get("/MyFirst/myPage").then().statusCode(OK);
    }
    
    @Test
    public void testMyPage2() {
        when().get("/MyFirst/myPage2/data").then().statusCode(OK).body(is("data"));
    }

    @Test
    public void testMyAction() {
        when().post("/MyFirst/myAction").then().statusCode(OK);
    }

}
