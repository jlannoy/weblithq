package test.controllers;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import io.weblith.core.results.Result;
import io.weblith.core.results.TextResult;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

@Controller
public class TextResultsController {

    // tag::examples[]
    @Get
    public Result simpleText() {
        return new TextResult("Hello there!");
    }

    @Get
    public Result textFromObject() {
        Object anObject = Arrays.asList("Hello", "there", "!");
        return new TextResult(anObject).contentType(MediaType.TEXT_PLAIN)
                                       .charset(StandardCharsets.US_ASCII)
                                       .status(Status.ACCEPTED);
    }
    // end::examples[]

    @Get
    public Result textFromException() {
        return new TextResult(new IllegalArgumentException("No hello there..."));
    }

}
