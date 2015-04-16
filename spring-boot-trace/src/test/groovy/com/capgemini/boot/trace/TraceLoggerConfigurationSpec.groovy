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
@WebIntegrationTest("logging.level.com.capgemini.boot.trace.TestApplication=TRACE")
class TraceLoggerConfigurationSpec extends Specification {

    @Value('${local.server.port}')
    int serverPort

	@Autowired
	ApplicationContext context

    RestTemplate restTemplate = new TestRestTemplate()

    def "annotation advisor bean exists with correct properties set"() {
        given:
		PointcutAdvisor bean = context.getBean("traceAnnotationAdvisor")
		
		expect:
        bean.pointcut instanceof ExpressionPointcut
        ((ExpressionPointcut) bean.pointcut).expression == "@annotation(com.capgemini.boot.trace.annotation.Trace)"
    }
	
	def "trace interceptor bean exists with correct properties set"() {
        given:
		Interceptor bean = context.getBean("traceInterceptor")
		
		expect:
        bean instanceof CustomizableTraceInterceptor
        with(((CustomizableTraceInterceptor) bean)) {
            enterMessage == 'Entering $[methodName]($[arguments])'
            exitMessage == 'Leaving  $[methodName](), returned $[returnValue]'
        }
	}

    def "can trace a method call"() {
        given:
        def output = new ByteArrayOutputStream()

        def origOut = System.out
        System.out = new PrintStream(output, true)

        when:
        restTemplate.getForEntity("http://localhost:$serverPort", String)

        then:
        output.toString().contains("Entering getMessage()")
        output.toString().contains("Leaving  getMessage(), returned Hello World!")

        cleanup:
        System.out = origOut
    }
}
