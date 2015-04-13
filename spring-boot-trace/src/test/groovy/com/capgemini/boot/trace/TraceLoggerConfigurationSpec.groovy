package com.capgemini.boot.trace

import org.aopalliance.intercept.Interceptor
import org.springframework.aop.PointcutAdvisor
import org.springframework.aop.interceptor.CustomizableTraceInterceptor
import org.springframework.aop.support.ExpressionPointcut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = TestApplication)
@WebAppConfiguration
@IntegrationTest
class TraceLoggerConfigurationSpec extends Specification {
	
	@Autowired
	ApplicationContext context

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
}
