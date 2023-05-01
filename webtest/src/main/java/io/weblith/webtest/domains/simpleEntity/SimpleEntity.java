package io.weblith.webtest.domains.simpleEntity;

import java.util.Date;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class SimpleEntity extends PanacheEntity {

    public enum Type {
        A,
        B,
        C
    }

    @NotEmpty
    public String name;

    @NotNull
    @Min(value = 1)
    public Integer quantity, quantity2;
    
    @NotNull
    public Date date;

    @NotNull
    @Enumerated(EnumType.STRING)
    public Type type;

    public SimpleEntity() {
        super();
    }

    // For test data generation
    public SimpleEntity(String name, Integer quantity, Date date, Type type) {
        super();
        this.name = name;
        this.quantity = quantity;
        this.quantity2 = 10;
        this.date = date;
        this.type = type;
    }

    public static SimpleEntity findByName(String name) {
        return find("name", name).firstResult();
    }

}