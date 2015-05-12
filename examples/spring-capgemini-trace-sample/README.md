Spring Boot Trace Sample
========================

Introduction
------------

This is a simple Video store application that allows users to

   * add a video to the store
   * retrieve all videos from the store
   * find a video by it's title
   
A Video has the following attributes

   * id
   * name
   * url
   * duration;


Design
------

### REST API

The Video store application is a Spring Boot Application, that as a Rest Controller class mapping the following URLs

   * `localhost:8080/video` (GET) - retrieve all videos from the store
   * `localhost:8080/video` (POST) - add a video to the store
   * `localhost:8080/video/find?title=MyVideo` (POST) - find all videos with title of "MyVideo"
   
In order to add a video, the url must pass a JSON payload containing the Video information.

`src/main/resources/addVideo.json` is a json file containing a single Video entry.  Use the 'curl' command to send a HTTP POST command containing the json defined in this file to the specified url.

`curl -H "Content-Type: application/json" --data @addVideo.json http://localhost:8080/video`
  
### JPA Repository

The application defines a repository that extends the Spring "CrudRepository".  The gradle build file defines a H2 database,  Spring Boot discovers it and and runs an in memory h2 database within the application.  The `VideoRepository` interface defines the application's interface to the database. There is no implementation of the `VideoRepository` in the project, Spring dynamically creates the implementation when it discovers the `@Repository` annotated interface.

On Spring Boot startup Spring JDBC finds, and executes the `data.sql` file located in the `src/main/resources` directory.  This file populates the repository with 3 Video entries.

### Trace starter

The video applications gradle build file declares the `spring-boot-trace` spring boot starter as a dependency.

This allows the use of the  `@EnableTraceLogging` annotation to state that this application can be configured to trace entry/exit/exception points of public methods, defined by configuration.

Configuration
-------------

### Logging configuration

The `spring-boot-trace` spring boot starter logs at `TRACE` level, so the default `LogBack` logger used by the Video Spring Boot application must be configured to capture logs at this `TRACE` level.  This can be configured in the configuration file in `src/main/resources/config/application.properties`, as follows

`logging.level.com.capgemini.boot.trace.sample.video.controller.VideoSvc=TRACE` - configures `TRACE` logging for only the VideoSvc class

OR

`logging.level.com.capgemini.boot.trace.sample.video.*=TRACE` - configures `TRACE` logging for all classes in the `com.capgemini.boot.trace.sample.video package` and its subpackages.


### Trace configuration

To enable trace logging, the following attribute in the `application.properties` file must be set to true

`trace-logging.enabled=true`

If, as in the video application example, the target object to be trace logged implements an interface, you must forces the use of CGLIB rather than JDK dynamic proxies by setting the following property in the `application.properties` file

`spring.aop.proxyTargetClass=true`

see [spring documentation](http://docs.spring.io/spring/docs/2.5.x/reference/aop.html#aop-proxying) for more information

### Trace pointcut configuration

The methods to be traced by the `spring-boot-trace` starter, are defined by AOP pointcut expressions in the following format in the `application.properties` file

`trace-logging.pointcut.NAME=execution(* com.capgemini.boot.trace.sample.video.controller.VideoSvc.*(..))

where NAME is used to differentiate pointcuts.

The Spring AOP pointcut expression language is defined, with examples, at the following [Spring documentation](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html#aop-pointcuts-examples)

The video application uses

`trace-logging.pointcut.videoSvc=execution(* com.capgemini.boot.trace.sample.video.controller.VideoSvc.*(..))`

which means that all public methods of the VideoSvc class are trace logged

The following examples enables tracing of all public methods in VideoSvc class that take a String parameter - i.e the method "Collection<Video> findByTitle(String title)"

`trace-logging.pointcut.test1=execution(* com.capgemini.boot.trace.sample.video.controller.VideoSvc.*(String))` 

   





