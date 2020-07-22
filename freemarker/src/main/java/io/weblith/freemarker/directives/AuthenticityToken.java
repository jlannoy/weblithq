package io.weblith.freemarker.directives;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import io.weblith.core.request.RequestContext;

@SuppressWarnings("rawtypes")
public class AuthenticityToken implements TemplateDirectiveModel {

    private final RequestContext context;

    public AuthenticityToken(RequestContext context) {
        super();
        this.context = context;
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {

        if (!params.isEmpty()) {
            throw new TemplateException("This directive doesn't allow parameters.", env);
        }

        if (loopVars.length != 0) {
            throw new TemplateException("This directive doesn't allow loop variables.", env);
        }

        Writer out = env.getOut();
        out.append(context.session().getAuthenticityToken().toString());

    }
}