package com.capgemini.boot.trace

import org.aopalliance.intercept.Interceptor
import org.springframework.aop.PointcutAdvisor
import org.springframework.aop.interceptor.CustomizableTraceInterceptor
import org.springframework.aop.support.ExpressionPointcut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = TestApplication)
@WebIntegrationTest(["logging.level.com.capgemini.boot.trace.TestApplication=TRACE",
					 "server.port=0",
					 "trace-logging.pointcut.test1=execution(* getMessageWithoutAnnotation())",
					 "trace-logging.pointcut.test2=execution(* getAnotherMessageWithoutAnnotation())"])
class TraceLoggerRegistrarSpec extends Specification {

    @Value('${local.server.port}')
    int serverPort

	@Autowired
	ApplicationContext context

    RestTemplate restTemplate = new TestRestTemplate()

    def "first pointcut advisor bean exists"() {
        given:
		PointcutAdvisor bean = context.getBean("test1Advisor")
		
		expect:
        bean.pointcut instanceof ExpressionPointcut
        ((ExpressionPointcut) bean.pointcut).expression == "execution(* getMessageWithoutAnnotation())"
    }
	
	def "second pointcut advisor bean exists"() {
		given:
		PointcutAdvisor bean = context.getBean("test2Advisor")
		
		expect:
		bean.pointcut instanceof ExpressionPointcut
		((ExpressionPointcut) bean.pointcut).expression == "execution(* getAnotherMessageWithoutAnnotation())"
	}

    def "method matching pointcut is traced"() {
		given:
		def output = new ByteArrayOutputStream()

		def origOut = System.out
		System.out = new PrintStream(output, true)

		when:
		restTemplate.getForEntity("http://localhost:$serverPort/nonAnnotated", String)

		then:
		output.toString().contains("Entering getMessageWithoutAnnotation()")
		output.toString().contains("Leaving  getMessageWithoutAnnotation(), returned Hello Non Annotated World!")

		cleanup:
		System.out = origOut
	}
	
	def "multiple pointcuts supported"() {
		given:
		def output = new ByteArrayOutputStream()

		def origOut = System.out
		System.out = new PrintStream(output, true)

		when:
		restTemplate.getForEntity("http://localhost:$serverPort/nonAnnotated", String)
		restTemplate.getForEntity("http://localhost:$serverPort/anotherNonAnnotated", String)

		then:
		output.toString().contains("Entering getMessageWithoutAnnotation()")
		output.toString().contains("Leaving  getMessageWithoutAnnotation(), returned Hello Non Annotated World!")
		output.toString().contains("Entering getAnotherMessageWithoutAnnotation()")
		output.toString().contains("Leaving  getAnotherMessageWithoutAnnotation(), returned Hello Non Annotated Universe!")

		cleanup:
		System.out = origOut
	}
}
