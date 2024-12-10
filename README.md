# Relations API java client Authz annotations PoC

This is a PoC for testing authorization filter annotations `@AuthzPreFilter` and `@AuthzPostFilter` available from relations-client-java on the [hackathon-filter-annotations-poc](https://github.com/project-kessel/relations-client-java/compare/main...hackathon-filter-annotations-poc) branch.

## Usage

By simply taking a method like
```java
public List<Widget> getWidgets() {
  return widgetRepository.getWidgets();
}
```
we can add authorization filtering of the results with some small modifications. In the below example, the method now "post" filters the results of `getWidgets()` repository call for widgets that the `user` has `view` access to.
```java
@AuthzPostFilter(permission = "view")
public List<Widget> getWidgets(UserPrincipal user) {
  return widgetRepository.getWidgets();
}
```

We can also query access first and "pre" filter the any calls to the database, so that only accessible widgets are retrieved.
```java
@AuthzPreFilter(permission = "view")
public List<Widget> getWidgets(UserPrincipal user) {
  return widgetRepository.getWidgets();
}
```
This option requires the use of the hibernate orm and some additional configuration so that the accessible widget ids can be mapped to the right database column.

For example, a filter must be added. It can be added to the `@Entity` class:
```java
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
```

## How does it work?

Access filtering is handled by an interceptor that is tied to the filter annotation. When placed on a
suitable method, results are filtered transparently to the user. A CDI container or runtime like Quarkus
is required.

Taking the method, below, as an example,
```java
public List<Widget> getWidgets() {
  return widgetRepository.getWidgets();
}
```
if we want to filter the returned list of widgets, we need to know a few things:
1. How `Widgets` are represented in the authz service queries.
2. How subjects of authorization queries are represented.
3. How the relevant authz permission is specified.

Answering these questions gives us the bones of our authz request that occurs behind the scenes.
It will query access by a) object/object type, b) relevant permission and c) subject of access. To 
provide this information  we decorate the method as follows:
```java
@AuthzPreFilter(permission = "view")
public List<Widget> getWidgets(UserPrincipal user) {
  return widgetRepository.getWidgets();
}
```
adding an annotation and an additional parameter of type `Principal`.

`b)` is easy, it's specified right there in the annotation (since the permission relates directly to the 
method).

`a)` is provided by a converter, which converts the Widget type and Widget objects to their equivalent
objects in the relations API requests. e.g.
```java
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
```
The converter just needs to be defined for the `Widget` type and the interceptor will find and inject
the bean dynamically when it is needed.

In the case of `c)` the interceptor looks for the first `Principal` implementation in the method parameters
that it can find a subject converter for. Similar to `a)`, the principal, e.g. `UserPrincipal` needs to 
be converter to a subject object that the relations API can understand.

### `@AuthzPreFilter`

The `@AuthzPreFilter` requires a small amount of additional config to ensure that accessible resources returned by the 
relations API can be used as a filter criterion for hibernate orm-managed database queries.

`@FilterDef` and `@Filter` annotations must be defined on the `@Entity`, as above.
```java
@Entity
@FilterDef(name = AuthzPreFilter.FILTER_NAME,
        parameters = @ParamDef(name = AuthzPreFilter.PARAM_NAME, type = String.class))
@Filter(name = AuthzPreFilter.FILTER_NAME, condition = "name IN (:id)")
public class Widget extends PanacheEntity {...}
```
The `condition` here is important, since it provides the mapping between the `id` representing 
accessible Widget ids in the relations API and the `name` representing the attribute, and database
column, in the local persistence.

## Summary of steps to get up and running

1. Put filter annotation on method, specifying the permission.
2. Add a principal implementation to the parameters.
3. Define a bean of type `ObjectRefConverter<ReturnType>` for the return type.
4. Define a bean of type `SubjectRefConverter<PrincipalType>` for the principal type parameter.
5. OPTIONAL for `@AuthzPreFilter`: add `@FilterDef` and `@Filter` annotations to the `@Entity` type.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/test-relations-client-annotations-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.
