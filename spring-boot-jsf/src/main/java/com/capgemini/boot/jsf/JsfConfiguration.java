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
package com.capgemini.boot.jsf;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.faces.application.Application;
import javax.faces.webapp.FacesServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;
import java.util.HashMap;
import java.util.Map;

import static com.capgemini.boot.jsf.ViewScope.SCOPE_VIEW;

/**
 * Spring boot auto configuration for JSF.
 * Contains custom scope configuration and registering
 * of the faces servlet itself.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(Application.class)
@AutoConfigureAfter({
        EmbeddedServletContainerAutoConfiguration.class
})
public class JsfConfiguration {

    public static final String FACES_SERVLET_BEAN_NAME = "Faces-Servlet";
    public static final String FACES_SERVLET_REGISTRATION_BEAN_NAME = "FACES_SERVLET_REGISTRATION_BEAN_NAME";
    public static final String CUSTOM_SCOPE_CONFIGURER_BEAN_NAME = "CUSTOM_SCOPE_CONFIGURER_BEAN_NAME";

    /**
     * Configure supporting classes for Spring View Scoped beans.
     */
    @Configuration
    public static class FacesServletSupportAutoConfiguration {

        /**
         * Factory method for the CustomScopeConfigurer which adds
         * a view scope.
         *
         * @return the CustomScopeConfigurer
         */
        @Bean(name = CUSTOM_SCOPE_CONFIGURER_BEAN_NAME)
        public static CustomScopeConfigurer customScopeConfigurer() {
            Map<String, Object> scopes = new HashMap<String, Object>();
            scopes.put(SCOPE_VIEW, new ViewScope());
            CustomScopeConfigurer result = new CustomScopeConfigurer();
            result.setScopes(scopes);
            return result;
        }
    }

    /**
     * Autoconfiguration for the Faces servlet itself.
     */
    @Configuration
    @ConditionalOnClass({
            ServletRegistration.class,
            FacesServlet.class
    })
    public static class FacesServletAutoConfiguration {

        /**
         * Factory method for the FacesServlet.
         *
         * @return the FacesServlet
         */
        @Bean(name = FACES_SERVLET_BEAN_NAME)
        public Servlet facesServlet() {
            return new FacesServlet();
        }

        /**
         * Factory method for the ServletRegistrationBean for the FacesServlet.
         *
         * @return the ServletRegistrationBean for the FacesServlet
         */
        @Bean(name = FACES_SERVLET_REGISTRATION_BEAN_NAME)
        public ServletRegistrationBean facesServletRegistrationBean() {
            ServletRegistrationBean bean = new ServletRegistrationBean();
            bean.setName(FACES_SERVLET_BEAN_NAME);
            bean.setServlet(facesServlet());

            return bean;
        }
    }
}