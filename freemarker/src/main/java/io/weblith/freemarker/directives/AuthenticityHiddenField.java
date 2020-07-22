package io.weblith.freemarker.directives;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import io.weblith.core.request.RequestContext;
import io.weblith.core.security.AuthenticityTokenFilter;

@SuppressWarnings("rawtypes")
public class AuthenticityHiddenField implements TemplateDirectiveModel {

    private RequestContext context;

    public AuthenticityHiddenField(RequestContext context) {
        this.context = context;
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        if (!params.isEmpty()) {
            throw new TemplateModelException("This directive doesn't allow parameters.");
        }

        if (loopVars.length != 0) {
            throw new TemplateModelException("This directive doesn't allow loop variables.");
        }

        Writer out = env.getOut();
        out.append("<input type=\"hidden\" name=\"" + AuthenticityTokenFilter.AUTHENTICITY_TOKEN + "\" value=\""
                + context.session().getAuthenticityToken() + "\" />");
    }
}