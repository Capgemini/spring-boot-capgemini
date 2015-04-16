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

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.AbstractTraceInterceptor;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import com.capgemini.boot.trace.config.TraceLoggerPointcut;

/**
 * Utility methods for trace logger configuration.
 * 
 * @author Craig Williams
 *
 */
public final class TraceLoggerConfigurationUtils {

    private static final String ADVISOR_BEAN_SUFFIX = "Advisor";

    /**
     * Private constructor as this class provides static methods only
     */
    private TraceLoggerConfigurationUtils() {

    }

    /**
     * Creates a trace interceptor for logging entry into and exit from methods.
     * 
     * @return The created trace interceptor
     */
    public static AbstractTraceInterceptor createTraceInterceptor() {
        CustomizableTraceInterceptor customizableTraceInterceptor = new CustomizableTraceInterceptor();
        customizableTraceInterceptor.setUseDynamicLogger(true);
        customizableTraceInterceptor
                .setEnterMessage("Entering $[methodName]($[arguments])");
        customizableTraceInterceptor
                .setExitMessage("Leaving  $[methodName](), returned $[returnValue]");
        return customizableTraceInterceptor;
    }

    /**
     * Registers an advisor bean based on a specified pointcut.
     * 
     * @param registry
     *            The bean registry where the advisor will be registered.
     * @param pointcut
     *            The pointcut for the advisor
     */
    public static void registerAdvisor(BeanDefinitionRegistry registry,
            TraceLoggerPointcut pointcut) {
        registry.registerBeanDefinition(
                createAdvisorBeanName(pointcut.getName()),
                createAdvisorBeanDefinition(pointcut.getPointcutExpression()));
    }

    private static BeanDefinition createAdvisorBeanDefinition(
            String pointcutExpression) {
        final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition
                .setConstructorArgumentValues(createAdvisorConstructorArguments(pointcutExpression));
        beanDefinition.setBeanClass(DefaultPointcutAdvisor.class);

        return beanDefinition;
    }

    private static ConstructorArgumentValues createAdvisorConstructorArguments(
            String pointcutExpression) {
        final ConstructorArgumentValues constructorValues = new ConstructorArgumentValues();

        final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(pointcutExpression);

        constructorValues.addIndexedArgumentValue(0, pointcut);
        constructorValues.addIndexedArgumentValue(1,
                TraceLoggerConfigurationUtils.createTraceInterceptor());

        return constructorValues;
    }

    private static String createAdvisorBeanName(String pointcutName) {
        return pointcutName + ADVISOR_BEAN_SUFFIX;
    }
}
