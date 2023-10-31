# Tracing in Spring Boot 3 with Zipkin and Kafka as a transport

Adding traces in Spring Boot 3 application (transported via Kafka). The functionality also allows observing app's
components interactions.

## Get started

- Install the project locally using

```bash
mvn clean install
```

- Then go to your project (where you want to use the starter) and add dependency;

```xml

<dependency>
    <groupId>ru.alfastrah.api</groupId>
    <artifactId>spring-boot-tracing-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

- Configure starter properties in `application.yaml` (see [properties description](#properties))

```yaml
custom:
  tracing:
    username: user
    password: pass
    topic: some-topic-name
    bootstrap-servers: server-1:port,server-2:port
```

- Enable tracing in `application.yaml` and set the sampling probability.
  Following properties belongs to `org.springframework.boot.actuate.autoconfigure.tracing.TracingProperties`.
  Property `management.tracing.enabled` enables/disables the above autoconfiguration
  via `@ConditionalOnProperty(name = "management.tracing.enabled", havingValue = "true")`

```yaml
management:
  tracing:
    enabled: true
    sampling:
      # choose a sampling probability between 0.0 and 1.0
      probability: 1.0 
```

- [_Optional_] Annotate your application components with `@Observed` annotation.
  This starter configures `io.micrometer.observation.aop.ObservedAspect`, that adds opportunity to trace interactions
  between application components with `@Observed` annotation. Example:

```java
public class SomeClass {
    @Observed(name = "something-that-method-does")
    public void foo() {
        System.out.println("bar");
    }
}
```

## Properties

| property                                | type      | description                                                                   | example                     |
|-----------------------------------------|-----------|-------------------------------------------------------------------------------|-----------------------------|
| custom.tracing.username                 | `String`  | kafka producer username                                                       | user                        |
| custom.tracing.password                 | `String`  | kafka producer password                                                       | pass                        |
| custom.tracing.topic                    | `String`  | kafka topic for traces                                                        | some-topic-name             |
| custom.tracing.bootstrap-servers        | `String`  | kafka bootstrap servers                                                       | server-1:port,server-2:port |
| management.tracing.enabled              | `Boolean` | enables autoconfiguration                                                     | true                        |
| management.tracing.sampling.probability | `Float`   | the probability of creating and saving traces for queries in your application | 1.0                         |