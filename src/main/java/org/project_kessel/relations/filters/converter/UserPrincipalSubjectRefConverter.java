package org.project_kessel.relations.filters.converter;

import com.sun.security.auth.UserPrincipal;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import org.project_kessel.api.relations.v1beta1.ObjectReference;
import org.project_kessel.api.relations.v1beta1.ObjectType;
import org.project_kessel.api.relations.v1beta1.SubjectReference;
import org.project_kessel.relations.converters.SubjectRefConverter;

@Unremovable
@ApplicationScoped
public class UserPrincipalSubjectRefConverter implements SubjectRefConverter<UserPrincipal> {
    static final String PRINCIPAL_OBJECT_TYPE = "user";
    static final String PRINCIPAL_OBJECT_NAMESPACE = "rbac";

    @Override
    public SubjectReference convert(UserPrincipal source) {
        return SubjectReference.newBuilder()
                .setSubject(ObjectReference.newBuilder()
                        .setType(ObjectType.newBuilder()
                                .setNamespace(PRINCIPAL_OBJECT_NAMESPACE)
                                .setName(PRINCIPAL_OBJECT_TYPE)
                                .build())
                        .setId(source.getName())
                        .build())
                .build();
    }
}
