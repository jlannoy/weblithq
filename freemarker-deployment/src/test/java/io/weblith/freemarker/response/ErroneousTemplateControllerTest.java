package io.weblith.freemarker.response;

//public class ErroneousTemplateControllerTest {
//
//    @RegisterExtension
//    static QuarkusUnitTest runner = new QuarkusUnitTest()
//            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
//                    .addClass(ErroneousTemplateController.class)
//                    .addAsResource("templates/ErroneousTemplateController/erroneous.ftlh")
//                    .addAsResource(new StringAsset("quarkus.http.test-port=0"), "application.properties"));
//    
//    @Test
//    public void testHelloPage() {
//        given()
//          .when().get("/TemplateError/hello")
//          .then()
//             .statusCode(500)
//             .body(containsString("FreemarkerRenderingException"));
//    }
//
//}