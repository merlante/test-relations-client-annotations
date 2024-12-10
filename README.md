# Relations java client Authz annotations PoC

This is a PoC for testing authorization filter annotations `@AuthzPreFilter` and `@AuthzPostFilter` available from relations-client-java on the [hackathon-filter-annotations-poc](https://github.com/project-kessel/relations-client-java/tree/hackathon-filter-annotations-poc|hackathon-filter-annotations-poc) branch.

By simply taking a method like
```java
public List<Widget> getWidgets() {
  return widgetRepository.getWidgets();
}
```
we can add authorization filtering of the results with some small modifications. In the below example, the method now "post" filters the results of `getWidgets()` repository call for widgets that the `user` has access to.
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
