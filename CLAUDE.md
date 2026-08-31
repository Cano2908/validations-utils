# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

`com.kuantik:validations-utils:1.0.0` — shared library for the k-guard validation platform. Consumed by `integrity-validator`, `validaciones-financial-validator`, and `k-orchestrator`. Contains the gRPC contract, domain model, rule engine, and XML parser used across all validators.

## Build & Test Commands

```bash
# Compile, test, and install to local Maven repo (~/.m2)
./mvnw clean install

# Compile only (also runs protoc code generation)
./mvnw compile

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Skip tests when installing
./mvnw install -DskipTests
```

## Architecture

### Package root: `com.kuantik.validation`

```
domain/
  model/       Rule, RuleResult, ValidationJob, ValidationResult
  port/        RuleExecutor  ← interface every rule must implement
  exception/   RuleExecutionException, XmlParseException

application/
  ValidateXmlUseCase  ← orchestrates rules; runs ALL rules regardless of severity (no fail-fast)

infrastructure/
  xml/         SecureXmlParser  ← XXE-safe DOM parser
  config/      RulesConfig  ← @Configuration that builds Map<String, RuleExecutor> from all beans

proto/
  validator.proto  → generates com.kuantik.grpc.v1.{ValidateRequest, ValidateResponse, FindingProto, ValidatorGrpc}
```

### How auto-configuration works

`RulesConfig` is registered in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. When a Spring Boot app includes this library as a dependency it automatically:
1. Triggers `@ComponentScan("com.kuantik.validation")` → picks up `SecureXmlParser` and `ValidateXmlUseCase`
2. Creates a `Map<String, RuleExecutor>` bean from all `RuleExecutor` beans in the context (including the consuming app's rules)

### Rule contract

Each rule in a consuming project must implement `RuleExecutor`:
```java
public interface RuleExecutor {
    String getCode();              // unique rule code, e.g. "INT-01"
    default String getSeverity() { return "ERROR"; }  // ERROR | WARNING
    RuleResult execute(Rule rule, Document xml);
}
```

`ValidateXmlUseCase` discovers all `RuleExecutor` beans, sorts them by code (TreeMap), and runs them in order. A `WARNING` failure does not affect the final status; an `ERROR` failure sets status to `PARTIAL`.

### gRPC contract

The proto generates server stubs (`ValidatorGrpc.ValidatorImplBase`) and client stubs (`ValidatorGrpc.ValidatorBlockingStub`). Each validator service extends `ValidatorImplBase`; `k-orchestrator` uses the blocking stub to call them.

## Using locally

After `./mvnw clean install`, add to any consuming project's `pom.xml`:

```xml
<dependency>
    <groupId>com.kuantik</groupId>
    <artifactId>validations-utils</artifactId>
    <version>1.0.0</version>
</dependency>
```

No additional `@ComponentScan` or `@Import` needed — auto-configuration handles it.

## Migrating a consuming project

1. Add the dependency above.
2. Delete the duplicated classes: `Rule`, `RuleResult`, `ValidationJob`, `ValidationResult`, `RuleExecutor`, `RuleExecutionException`, `XmlParseException`, `SecureXmlParser`, `RulesConfig`, `ValidateXmlUseCase`.
3. Update imports from `com.kuantik.<service_package>.*` → `com.kuantik.validation.*`.
4. Remove the `validator.proto` file and protobuf plugin (already compiled and bundled in this library).
5. Keep: `ValidationRequestMapper`, the `GrpcService`, and all rule implementations — those stay per-service.
