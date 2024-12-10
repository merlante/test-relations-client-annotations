package org.project_kessel.relations.filters.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.project_kessel.relations.annotations.AuthzPreFilter;

@Entity
@FilterDef(name = AuthzPreFilter.FILTER_NAME,
        parameters = @ParamDef(name = AuthzPreFilter.PARAM_NAME, type = String.class))
@Filter(name = AuthzPreFilter.FILTER_NAME, condition = "name IN (:id)")
public class Widget extends PanacheEntity {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
