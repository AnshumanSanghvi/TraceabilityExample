# Read Me First

### Demoed in the project

- Use Spring cloud gateway + spring cloud sleuth to generate unique traceId + spanId for requests. Propagate these to microservices.
- Enable `reactor.netty.http.client` logs to match that traceIds and spanIds produced at the gateway match those of the microservice.
- Configure a custom thread pool task executor so that each new thread executed with it, can have same traceId but new spanId.
- Configure displaying traceId and spanId for `@Async` methods.
- Manage spanIds using `@NewSpan`.
