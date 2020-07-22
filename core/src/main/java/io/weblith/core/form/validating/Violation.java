package io.weblith.core.form.validating;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Path.Node;

public class Violation {

    private final String messageKey;

    private String fieldKey;

    private final String defaultMessage;

    private final Object[] messageParams;

    public Violation(Throwable throwable, String fieldKey) {
        throwable = throwable.getCause() != null ? throwable.getCause() : throwable;
        this.messageKey = escapeKey(throwable.getClass().getSimpleName());
        this.fieldKey = fieldKey;
        this.defaultMessage = throwable.getLocalizedMessage();
        this.messageParams = new Object[] {};
    }

    public Violation(String messageKey, String fieldKey, String defaultMessage, Object... messageParams) {
        this.messageKey = escapeKey(messageKey);
        this.fieldKey = fieldKey;
        this.defaultMessage = defaultMessage != null && fieldKey != null ? defaultMessage.replace("{0}", fieldKey)
                : defaultMessage;
        this.messageParams = messageParams;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public Object[] getMessageParams() {
        return messageParams;
    }

    public static Violation of(javax.validation.ConstraintViolation<?> cv) {
        // return new Violation(cv.getMessageTemplate(), Iterables.getLast(cv.getPropertyPath()).getName(),
        // cv.getMessage(), cv.getExecutableParameters());

        // TODO ParameterNameProvider ?
        List<Node> nodes = StreamSupport
                .stream(cv.getPropertyPath().spliterator(), false)
                .collect(Collectors.toList());
        if (nodes.size() > 2) {
            nodes = nodes.subList(2, nodes.size());
        }

        return new Violation(cv.getMessageTemplate(), nodes.stream().map(Node::getName).collect(Collectors.joining(".")),
                cv.getMessage(), cv.getExecutableParameters());
    }

    protected static final String escapeKey(String key) {
        return key.replaceAll("[^a-zA-Z0-9.]", "");
    }

}
