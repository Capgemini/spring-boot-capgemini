package com.capgemini.boot.trace;

import org.aopalliance.intercept.Interceptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.capgemini.boot.trace.TraceLoggerConfigurationTests.Application;
import com.capgemini.boot.trace.annotation.EnableTraceLogger;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TraceLoggerConfigurationTests {	
	
	@Autowired
	ApplicationContext context;
	
	@Test
	public void testThatAnnotationAdvisorExists() {
		final PointcutAdvisor bean = (PointcutAdvisor) context.getBean("traceAnnotationAdvisor");
		Assert.assertNotNull("Annotation advisor bean is null", bean);
	}
	
	@Test
	public void testThatTraceInterceptorExists() {
		final Interceptor bean = (Interceptor) context.getBean("traceInterceptor");
		Assert.assertNotNull("Trace interceptor bean is null", bean);
	}
	
	@Configuration
	@EnableAutoConfiguration
	@EnableTraceLogger
	protected static class Application {	
		
		public static void main(String[] args) {
			new SpringApplicationBuilder(Application.class).properties(
					"spring.application.name=traceLogger").run(args);
		}
	}
}
