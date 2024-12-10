package org.project_kessel.relations.filters.converter;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import org.project_kessel.api.relations.v1beta1.ObjectReference;
import org.project_kessel.api.relations.v1beta1.ObjectType;
import org.project_kessel.relations.converters.ObjectRefConverter;
import org.project_kessel.relations.filters.model.Widget;

@Unremovable
@ApplicationScoped
public class WidgetObjectRefConverter implements ObjectRefConverter<Widget> {
    static final String WIDGET_OBJECT_TYPE = "thing";
    static final String WIDGET_OBJECT_NAMESPACE = "rbac";

    @Override
    public ObjectType objectType() {
        return ObjectType.newBuilder()
                .setName(WIDGET_OBJECT_TYPE)
                .setNamespace(WIDGET_OBJECT_NAMESPACE)
                .build();
    }

    @Override
    public ObjectReference convert(Widget source) {
        return ObjectReference.newBuilder()
                .setType(objectType())
                .setId(source.getName())
                .build();
    }
}
