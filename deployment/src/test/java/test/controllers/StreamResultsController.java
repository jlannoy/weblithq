package test.controllers;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import io.weblith.core.logging.NotLogged;
import io.weblith.core.results.Result;
import io.weblith.core.results.StreamResult;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;

@NotLogged
@Controller
public class StreamResultsController {

    public final static String MY_TXT_FILE = "META-INF/resources/my-file.txt";

    // tag::examples[]
    @Get
    public Result file() throws URISyntaxException {
        File file = new File(this.getClass()
                                 .getResource("/META-INF/resources/my-file.txt")
                                 .toURI());
        return new StreamResult(file);
    }

    @Get
    public Result url() {
        URL path = this.getClass()
                       .getResource("/META-INF/resources/my-file.txt");
        return new StreamResult(path);
    }
    // end::examples[]

    public final static String MY_UNKNOWN_TYPE_FILE = "META-INF/resources/my-file.properties";

    @Get
    public Result unknownTypeFile() throws URISyntaxException {
        URL path = this.getClass()
                       .getResource("/" + MY_UNKNOWN_TYPE_FILE);
        Objects.requireNonNull(path);
        return new StreamResult(new File(path.toURI()));
    }

    @Get
    public Result unknownTypeUrl() {
        URL path = this.getClass()
                       .getResource("/" + MY_UNKNOWN_TYPE_FILE);
        Objects.requireNonNull(path);
        return new StreamResult(path);
    }

}
