package io.weblith.webtest.domains.simpleEntity;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

public class SimpleEntitiesForm {

    public List<NestedForm> entities;

    public static class NestedForm {

        @NotEmpty
        public String name;

        public Date date;

        public SimpleEntity.Type type;
    }

}