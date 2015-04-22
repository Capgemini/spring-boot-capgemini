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
import org.springframework.aop.interceptor.CustomizableTraceInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate

import spock.lang.Specification

/*
 * NOTE: This could hopefully be moved into TraceLoggerConfigurationSpec once dynamic
 * properties functionality has been implemented.
 */
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = TestApplication)
@WebIntegrationTest(["logging.level.com.capgemini.boot.trace.TestApplication=TRACE", "server.port=0",
                     "trace-logging.message.enter=Entering customized \$[methodName](\$[arguments])",
                     "trace-logging.message.exit=Exiting customized \$[methodName](), returned \$[returnValue]",
                     "trace-logging.message.exception=Exception customized when executing \$[methodName](): \$[exception]"])
class TraceLoggerConfigurationMessagesSpec extends Specification {
        
    @Value('${local.server.port}')
    int serverPort

	@Autowired
	ApplicationContext context

    RestTemplate restTemplate = new TestRestTemplate()
	
	def "trace interceptor bean exists with correct properties set when messages configured"() {
        given:
		Interceptor bean = context.getBean("traceInterceptor")
		
		expect:
        bean instanceof CustomizableTraceInterceptor
        with(((CustomizableTraceInterceptor) bean)) {
            enterMessage == 'Entering customized $[methodName]($[arguments])'
            exitMessage == 'Exiting customized $[methodName](), returned $[returnValue]'
            exceptionMessage == 'Exception customized when executing \$[methodName](): \$[exception]'
        }
	}

    def "can configure custom entry and exit log messages"() {
        given:
        def output = new ByteArrayOutputStream()

        def origOut = System.out
        System.out = new PrintStream(output, true)

        when:
        restTemplate.getForEntity("http://localhost:$serverPort", String)

        then:
        output.toString().contains("Entering customized getMessageWithAnnotation()")
        output.toString().contains("Exiting customized getMessageWithAnnotation(), returned Hello World!")

        cleanup:
        System.out = origOut
    }
    
    def "can configure custom exception message"() {
        given:
        def output = new ByteArrayOutputStream()

        def origOut = System.out
        System.out = new PrintStream(output, true)

        when:
        restTemplate.getForEntity("http://localhost:$serverPort/exception", String)

        then:
        output.toString().contains("Entering customized getMessageWithAnnotationThrowingException()")
        output.toString().contains("Exception customized when executing getMessageWithAnnotationThrowingException(): java.lang.RuntimeException")

        cleanup:
        System.out = origOut
    }
}