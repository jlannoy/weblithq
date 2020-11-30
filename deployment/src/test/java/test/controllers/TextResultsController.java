package test.controllers;

import java.util.Arrays;

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
        return new TextResult(Arrays.asList("Hello", "there", "!"));
    }
    // end::examples[]

    @Get
    public TextResult getTextResultFromException() {
        return new TextResult(new IllegalArgumentException("No hello there..."));
    }

}
