package io.weblith.freemarker.methods;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ocpsoft.prettytime.PrettyTime;

import freemarker.template.SimpleDate;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import io.weblith.core.i18n.LocaleHandler;

@Singleton
public class PrettyTimeMethod implements TemplateMethodModelEx {

    private final LocaleHandler localeHandler;

    @Inject
    public PrettyTimeMethod(LocaleHandler localeHandler) {
        this.localeHandler = localeHandler;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public TemplateModel exec(List args) throws TemplateModelException {
        Date date = getFormattableObject(args.get(0));
        String result = new PrettyTime(localeHandler.current()).format(date);
        return new SimpleScalar(result);
    }

    private Date getFormattableObject(Object value) {
        if (value instanceof SimpleDate) {
            return ((SimpleDate) value).getAsDate();
        } else {
            throw new RuntimeException("Formattable object for PrettyTime not found!");
        }
    }
}
