/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
@WebIntegrationTest(["logging.level.com.capgemini.boot.trace.TestApplication=TRACE", "server.port=0"])
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
        ((ExpressionPointcut) bean.pointcut).expression == "@annotation(com.capgemini.boot.trace.annotation.Trace) or within(@com.capgemini.boot.trace.annotation.Trace *)"
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

    def "can trace an annotated method call"() {
        given:
        def output = new ByteArrayOutputStream()

        def origOut = System.out
        System.out = new PrintStream(output, true)

        when:
        restTemplate.getForEntity("http://localhost:$serverPort", String)

        then:
        output.toString().contains("Entering getMessageWithAnnotation()")
        output.toString().contains("Leaving  getMessageWithAnnotation(), returned Hello World!")

        cleanup:
        System.out = origOut
    }
	
	def "non annotated method isn't traced"() {
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
    
    def "can trace a class annotated method call"() {
        given:
        def output = new ByteArrayOutputStream()

        def origOut = System.out
        System.out = new PrintStream(output, true)

        when:
        restTemplate.getForEntity("http://localhost:$serverPort/classAnnotated", String)

        then:
        output.toString().contains("Entering getMessage()")
        output.toString().contains("Leaving  getMessage(), returned Hello World!")

        cleanup:
        System.out = origOut
    }
}
