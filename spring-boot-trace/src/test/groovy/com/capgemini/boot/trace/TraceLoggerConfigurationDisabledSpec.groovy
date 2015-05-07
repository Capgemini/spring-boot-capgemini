package com.capgemini.boot.trace

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate

import spock.lang.Specification

/*
 * NOTE: This could hopefully be moved into TraceLoggerConfigurationSpec once dynamic
 * properties functionality has been implemented.
 */
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = TestApplication)
@WebIntegrationTest(["logging.level.com.capgemini.boot.trace.TestApplication=TRACE", "server.port=0", 
                     "trace-logging.enabled=false",
                     "trace-logging.pointcut.test1=execution(* getMessageWithoutAnnotation())"])
class TraceLoggerConfigurationDisabledSpec extends Specification {

    @Value('${local.server.port}')
    int serverPort

    RestTemplate restTemplate = new TestRestTemplate()
    
    def "methods aren't traced when disabled"() {
        given:
        def output = new ByteArrayOutputStream()

        def origOut = System.out
        System.out = new PrintStream(output, true)

        when:
        restTemplate.getForEntity("http://localhost:$serverPort", String)

        then:
        !output.toString().contains("Entering getMessageWithAnnotation()")
        !output.toString().contains("Leaving  getMessageWithAnnotation(), returned Hello World!")

        cleanup:
        System.out = origOut
    }
    
    def "method matching pointcut is not traced when disabled"() {
        given:
        def output = new ByteArrayOutputStream()

        def origOut = System.out
        System.out = new PrintStream(output, true)

        when:
        restTemplate.getForEntity("http://localhost:$serverPort/nonAnnotated", String)

        then:
        !output.toString().contains("Entering getMessageWithoutAnnotation()")
        !output.toString().contains("Leaving  getMessageWithoutAnnotation(), returned Hello Non Annotated World!")

        cleanup:
        System.out = origOut
    }
}
