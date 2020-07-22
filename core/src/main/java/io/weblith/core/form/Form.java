package io.weblith.core.form;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import javax.enterprise.inject.spi.CDI;
import javax.validation.Validator;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.weblith.core.form.validating.ViolationsHolder;

@RegisterForReflection
public final class Form<T> extends ViolationsHolder {

    private final Class<T> valueClass;

    private Optional<T> content;

    private Form(Class<T> contentClass) {
        super();
        this.valueClass = contentClass;
        this.content = Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Form(T object) {
        super();
        this.valueClass = (Class<T>) object.getClass();
        this.content = Optional.ofNullable(object);
    }

    public T getValue() {
        return content.orElse(getDefaultValue());
    }

    public Class<T> getValueClass() {
        return this.valueClass;
    }

    private T getDefaultValue() {
        try {
            return valueClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setValue(T value) {
        this.content = Optional.ofNullable(value);
    }

    public String getValueName() {
        return getValueClass().getSimpleName();
    }

    public String getFormName() {
        return getValueName() + "Form";
    }

    public boolean validate() {
        if (this.content.isPresent()) {
            Validator validator = CDI.current().select(Validator.class).get();

            validator.validate(this.content.get())
                    .forEach(cv -> this.addViolation(io.weblith.core.form.validating.Violation.of(cv)));
        }
        return !this.hasViolations();
    }

    public final static <T> Form<T> of(Class<T> clazz) {
        return new Form<T>(clazz);
    }

    public final static <T> Form<T> of(T object) {
        return new Form<T>(object);
    }
}
