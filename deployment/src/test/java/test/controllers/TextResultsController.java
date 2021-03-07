package test.controllers;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import io.weblith.core.results.TextResult;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;

@Controller
public class TextResultsController {

    // tag::examples[]
    @Get
    public TextResult getSimpleTextResult() {
        return new TextResult("Hello there!");
    }

    @Get
    public TextResult getTextResultFromObject() {
        Object anObject = Arrays.asList("Hello", "there", "!");
        return new TextResult(anObject).contentType(MediaType.APPLICATION_JSON)
                                       .charset(StandardCharsets.US_ASCII)
                                       .status(Status.ACCEPTED);
    }
    // end::examples[]

    @Get
    public TextResult getTextResultFromException() {
        return new TextResult(new IllegalArgumentException("No hello there..."));
    }

}
