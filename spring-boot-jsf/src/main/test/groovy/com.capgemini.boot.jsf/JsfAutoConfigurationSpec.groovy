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
package com.capgemini.boot.jsf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.CustomScopeConfigurer
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.faces.webapp.FacesServlet
/**
 * Test specification for the JsfAutoConfiguration class
 */
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = TestApplication)
@WebIntegrationTest
class JsfAutoConfigurationSpec extends Specification {

    @Autowired
    ApplicationContext context

    def "faces servlet bean exists"() {
        given:
        FacesServlet bean = context.getBean(JsfAutoConfiguration.FACES_SERVLET_BEAN_NAME)

        expect:
        bean != null
    }

    def "servlet registration bean exists"() {
        given:
        ServletRegistrationBean bean = context.getBean(JsfAutoConfiguration.FACES_SERVLET_REGISTRATION_BEAN_NAME)

        expect:
        bean != null
    }

    def "servlet registration bean exists with faces servlet registered on it"() {
        given:
        ServletRegistrationBean bean = context.getBean(JsfAutoConfiguration.FACES_SERVLET_REGISTRATION_BEAN_NAME)

        expect:
        bean.getServletName().equals(JsfAutoConfiguration.FACES_SERVLET_BEAN_NAME)
    }

    def "custom scope configurer bean exists"() {
        given:
        CustomScopeConfigurer bean = context.getBean(JsfAutoConfiguration.CUSTOM_SCOPE_CONFIGURER_BEAN_NAME)

        expect:
        bean != null
    }

    def "custom scope configurer bean exists with view scope in scopes map"() {
        given:
        CustomScopeConfigurer bean = context.getBean(JsfAutoConfiguration.CUSTOM_SCOPE_CONFIGURER_BEAN_NAME)
        Map<String, Object> scopeMap = bean.scopes
        ViewScope scope = scopeMap.get("view")

        expect:
        scope instanceof ViewScope
    }
}