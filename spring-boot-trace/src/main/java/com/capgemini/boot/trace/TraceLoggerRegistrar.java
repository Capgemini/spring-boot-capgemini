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
package com.capgemini.boot.trace;

import java.util.Map;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;

import com.capgemini.boot.trace.settings.TraceLoggerSettings;

/**
 * Configures trace logging for enabled spring-boot applications for configured
 * pointcuts.
 * 
 * Pointcuts are configured via trace-logging.pointcut.[name] properties.
 */
public class TraceLoggerRegistrar extends
        SettingsBackedRegistrar<TraceLoggerSettings> {

    private static final String ADVISOR_BEAN_SUFFIX = "Advisor";

    @Override
    protected String getPropertyPrefix() {
        return TraceLoggerSettings.SETTINGS_PREFIX;
    }

    /**
     * Registers advisors based on configured pointcuts
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (getSettings().getEnabled()) {
            for (Map.Entry<String, String> pointcut : getSettings()
                    .getPointcut().entrySet()) {
                registerAdvisor(registry, pointcut.getKey(),
                        pointcut.getValue());
            }
        }
    }

    /**
     * Registers an advisor bean based on a specified pointcut.
     * 
     * @param registry
     *            the bean registry where the advisor will be registered.
     * @param name
     *            the name of the advisor
     * @param expression
     *            the expression of the advisor
     */
    private void registerAdvisor(BeanDefinitionRegistry registry, String name,
            String expression) {
        registry.registerBeanDefinition(createAdvisorBeanName(name),
                createAdvisorBeanDefinition(expression));
    }

    private BeanDefinition createAdvisorBeanDefinition(String pointcutExpression) {
        final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setConstructorArgumentValues(createAdvisorConstructorArguments(pointcutExpression));
        beanDefinition.setBeanClass(DefaultPointcutAdvisor.class);

        return beanDefinition;
    }

    private ConstructorArgumentValues createAdvisorConstructorArguments(String pointcutExpression) {
        final ConstructorArgumentValues constructorValues = new ConstructorArgumentValues();

        final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(pointcutExpression);

        constructorValues.addIndexedArgumentValue(0, pointcut);
        constructorValues.addIndexedArgumentValue(1, TraceLoggerConfigurationUtils.createTraceInterceptor(getSettings()));

        return constructorValues;
    }

    private String createAdvisorBeanName(String pointcutName) {
        return pointcutName + ADVISOR_BEAN_SUFFIX;
    }
}
