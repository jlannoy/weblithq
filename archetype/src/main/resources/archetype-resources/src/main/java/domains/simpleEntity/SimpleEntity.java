#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domains.simpleEntity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

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