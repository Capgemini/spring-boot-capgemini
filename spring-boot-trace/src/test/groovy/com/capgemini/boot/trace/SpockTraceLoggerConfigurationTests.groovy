package com.capgemini.boot.trace

import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration

import spock.lang.Specification

@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = TestApplication.class)
@WebAppConfiguration
@IntegrationTest
class SpockTraceLoggerConfigurationTests extends Specification {
	
	@Autowired
	ApplicationContext context;
	
	def "Test that the Annotation advisor bean exists"() {
		PointcutAdvisor bean = context.getBean("traceAnnotationAdvisor");
		
		expect:
			bean != null;
	}
	
	def "Test that the Trace Interceptor bean exists"() {
		Interceptor bean = context.getBean("traceInterceptor");
		
		expect:
			bean != null;
	}
}
