package org.project_kessel.relations.filters;

import com.sun.security.auth.UserPrincipal;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.project_kessel.relations.filters.model.Widget;
import org.project_kessel.relations.filters.service.WidgetService;

import java.util.List;

@QuarkusTest
public class AuthzPostFilterTest {
    @Inject
    WidgetService widgetService;

    @Test
    void testGetWidgets() {
        UserPrincipal user = new UserPrincipal("jeff");

        List<Widget> widgets = widgetService.getWidgets(user);

        // print to console
        widgets.forEach(widget -> System.out.println(widget.getName()));
    }
}
