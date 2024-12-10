package org.project_kessel.relations.filters.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.project_kessel.relations.filters.model.Widget;

import java.util.List;

@ApplicationScoped
public class WidgetRepository {

    public List<Widget> getWidgets() {
        return Widget.listAll();
    }
}
