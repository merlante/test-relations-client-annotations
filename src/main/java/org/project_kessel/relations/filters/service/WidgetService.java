package org.project_kessel.relations.filters.service;

import com.sun.security.auth.UserPrincipal;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.project_kessel.relations.annotations.AuthzPreFilter;
import org.project_kessel.relations.filters.model.Widget;
import org.project_kessel.relations.filters.repository.WidgetRepository;

import java.util.List;

@ApplicationScoped
public class WidgetService {
    @Inject
    WidgetRepository widgetRepository;

    @AuthzPreFilter(permission = "view")
    public List<Widget> getWidgets(UserPrincipal user) {
        return widgetRepository.getWidgets();
    }

}
