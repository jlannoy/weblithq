package io.weblith.freemarker.methods;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import io.weblith.core.form.validating.Violation;
import io.weblith.core.i18n.Messages;

public class I18nMethod implements TemplateMethodModelEx {

    private final static Logger LOGGER = Logger.getLogger(I18nMethod.class);

    private final Messages messages;

    public I18nMethod(Messages messages) {
        super();
        this.messages = messages;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public TemplateModel exec(List args) throws TemplateModelException {

        if (args.size() == 1 && args.get(0) instanceof StringModel
                && ((StringModel) args.get(0)).getWrappedObject() instanceof Violation) {

            Violation violation = (Violation) ((StringModel) args.get(0)).getWrappedObject();

            String messageValue = messages.get(violation.getMessageKey(), violation.getMessageParams())
                    .orElse(violation.getDefaultMessage());

            return new SimpleScalar(messageValue);

        } else if (args.size() == 1) {

            String messageKey = ((SimpleScalar) args.get(0)).getAsString();

            String messageValue = messages.get(messageKey).orElse(messageKey);

            logIfMessageKeyIsMissing(messageKey, messageValue);

            return new SimpleScalar(messageValue);

        } else if (args.size() > 1) {

            List<String> strings = new ArrayList<>();

            for (Object o : args) {

                // Allow only numbers and strings as arguments
                if (o instanceof SimpleScalar) {
                    strings.add(((SimpleScalar) o).getAsString());
                } else if (o instanceof SimpleNumber) {
                    strings.add(((SimpleNumber) o).toString());
                }

            }

            String messageKey = strings.get(0);

            String messageValue = messages.get(messageKey, strings.subList(1, strings.size()).toArray()).orElse(messageKey);

            logIfMessageKeyIsMissing(messageKey, messageValue);

            return new SimpleScalar(messageValue);

        } else {
            throw new TemplateModelException("Cannot use the i18n method without any key");
        }

    }

    public void logIfMessageKeyIsMissing(String messageKey, String messageValue) {

        if (messageKey.equals(messageValue)) {
            LOGGER.warnv("Message key {0} missing ; using the key as value instead", messageKey);
        }

    }
}
