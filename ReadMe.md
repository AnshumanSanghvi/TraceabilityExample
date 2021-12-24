# Gateway Multi-Project

### Traceability across a system

#### Monolith Architecture

Unless we explicitly plan it, it can be hard to correlate the error message of an exception with contextual and diagnostic information of the broader business process, especially if the process has multiple code points in a long workflow.

For example, consider a business case, that when a user modifies . If an exception is thrown at any of these points, it would be useful to have a single point of correlation between various log entries across the workflow, such as userId in this case.
 
#### Microservice Architecture

The difficulty in correlation is especially prominent in the case where multiple components are involved. 

For example, it would be difficult to track an API request that first goes to the API gateway, then through multiple microservices, and eventually returns a response back from the API gateway. Collecting logs from multiple components and then matching them for a single request would be very tricky.

One way to fix this issue is to have a unique ID (e.g a unique request ID for each API call) that is passed across all the components or steps in the workflow, so that we can correlate all the logs of a single business scenario. All the exceptions can then include the ID in the log message. Then you can search for this ID on a ELK stack or Prometheus setup or ZipKin server when debugging exceptions.

Generally we pass this request ID from the API gateway (or whichever component the request originates from) to all the microservices, and this adds traceability in a microservice architecture.

### Mapped Diagnostic Context

SLF4J / Log4J2 and others, provide the option of Mapped Diagnostic Context (MDC), which allows us to provide a map (keys and values) of data. This data is stored in the ThreadContext (`org.apache.logging.log4j.ThreadContext` for Log4J2), which is unique for each thread. At the time of generating/executing the log statement, the MDC values are injected into the log.

The simplest way to add traceability is to generate a unique id, and add it to the MDC.

Configuring the log format with MDC keys (see `log4j2.xml`) allows for structured logging that is useful when querying logs.

#### Some common scenarios of using MDC:

1. Logging traceId for API calls in a microservice architecture.
   - Using libraries like SpringCloudSleuth we can get traceId and spanId for each API calls, Kafka calls and [others](https://docs.spring.io/spring-cloud-sleuth/docs/current-SNAPSHOT/reference/html/integrations.html#sleuth-integration)  
2. Logging sessionId, userId for a user initiated events.
   - E.g: Pass the userId for each user initiated flow, add the userId as MDC, so that all log statements in the same thread will contain that userId. For projects with SpringSecurity, we can use `SecurityContextHolder` to get a hold of the `Principal` and extract userId or username to the MDC.
3. Logging code-path/traceId for scheduled jobs or async jobs
    - E.g: Can add fields like `job_status`, `job_id` and `job_records_processed` for scheduled/batch jobs. 
4. Logging "origin" when a component of workflow can be initiated from multiple sources.
5. Logging root cause to error logs 
    - E.g: for the case if exceptions are swallowed or wrapped & rethrown or handled globally. Now we can search or get stats from the log files about frequency of specific exceptions. For SpringBoot + SpringWeb, using `@ControllerAdvice` + `@ExceptionHandler`, all exceptions thrown to Web Controllers can be handled at a single place, where we can put the rootCause in the MDC.
6. Logging duration/profiling to workflows 
    - E.g: create a switch or annotation that will profile your code, and append that profiling data to your log statements.


### Spring Cloud Sleuth

Sleuth is a library that makes it possible to identify logs pertaining to a specific job, thread, or request. Sleuth integrates effortlessly with logging frameworks like Logback and SLF4J to add unique identifiers that help track and diagnose issues using logs.

Spring Cloud Sleuth uses Brave as the tracing library that adds unique ids to each web request that enters our application. Furthermore, the Spring team has added support for sharing these ids across thread boundaries.

**Traces** can be thought of like a single request or job that is triggered in an application. All the various steps in that request, even across application and thread boundaries, will have the same traceId.

**Spans**, on the other hand, can be thought of as sections of a job or request. A single trace can be composed of multiple spans each correlating to a specific step or section of the request. Using trace and span ids we can pinpoint exactly when and where our application is as it processes a request.

---

### Application

#### Gateway

##### Demoed in this project:

- Use Spring cloud gateway + spring cloud sleuth to generate unique traceId + spanId for requests. Propagate these to microservices.
- Enable `reactor.netty.http.client` logs to match that traceIds and spanIds produced at the gateway match those of the microservice.
- Configure a custom thread pool task executor so that each new thread executed with it, can have same traceId but new spanId.
- Configure displaying traceId and spanId for `@Async` methods.
- Manage spanIds using `@NewSpan`.

#### Gateway endpoints: 

- `http://localhost:9000/`
- `http://localhost:9000/new-thread`
- `http://localhost:9000/async`
- `http://localhost:9000/annotated`

#### MicroService endpoints via gateway:

- `http://localhost:9000/ms/test`
- `http://localhost:9000/ms/divide?numerator=10&denominator=5`

#### MicroService

##### Demoed in this project:

- Read traceId from request headers using request filtering as well as controller method parameters
- Log traceId, spanId, duration, and root cause using MDC.
- Configure log4j2.xml to display it MDC values.

##### MicroService endpoints:

- `http://localhost:9001/test`
- `http://localhost:9001/divide?numerator=10&denominator=5`

---

#### References

- https://docs.spring.io/spring-cloud-sleuth/docs/current-SNAPSHOT/reference/html/index.html
- https://www.baeldung.com/spring-cloud-sleuth-single-application
- https://reflectoring.io/tracing-with-spring-cloud-sleuth/
- https://www.baeldung.com/mdc-in-log4j-2-logback






