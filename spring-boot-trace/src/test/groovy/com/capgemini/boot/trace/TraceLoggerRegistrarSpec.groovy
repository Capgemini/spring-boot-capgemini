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

import org.springframework.aop.PointcutAdvisor
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
					 "trace-logging.pointcut[0].name=test1",
					 "trace-logging.pointcut[0].pointcutExpression=execution(* getMessageWithoutAnnotation())",
		             "trace-logging.pointcut[1].name=test2",
					 "trace-logging.pointcut[1].pointcutExpression=execution(* getAnotherMessageWithoutAnnotation())"])
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
