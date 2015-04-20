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

import com.capgemini.boot.trace.config.TraceLoggerConfig;
import com.capgemini.boot.trace.config.TraceLoggerConfigFactory;
import com.capgemini.boot.trace.config.TraceLoggerPointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * Configures trace logging for enabled spring-boot applications for configured
 * pointcuts.
 * 
 * Pointcuts are configured via trace-logging.pointcut.[name] properties.
 */
public class TraceLoggerRegistrar implements ImportBeanDefinitionRegistrar,
        EnvironmentAware {
    
    private static final String ADVISOR_BEAN_SUFFIX = "Advisor";

    /** User configuration for the trace logger */
    private TraceLoggerConfig config;

    /** Used to construct config */
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Registers advisors based on configured pointcuts
     */
    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry) {
        if (getConfig().isEnabled()) {
            registerAdvisors(registry);
        }
    }

    protected TraceLoggerConfig getConfig() {
        if (config == null) {
            // Ideally the config would be autowired but
            // 'registerBeanDefinitions' gets called
            // before beans are AutoWired so this is not possible (or will be
            // tricky at least)
            config = TraceLoggerConfigFactory.createConfig(environment);
        }

        return config;
    }

    private void registerAdvisors(BeanDefinitionRegistry registry) {
        final List<TraceLoggerPointcut> pointcuts = getConfig()
                .getConfiguredPointcuts();

        for (TraceLoggerPointcut pointcut : pointcuts) {
            registerAdvisor(registry, pointcut);
        }
    }
    
    /**
     * Registers an advisor bean based on a specified pointcut.
     * 
     * @param registry
     *            The bean registry where the advisor will be registered.
     * @param pointcut
     *            The pointcut for the advisor
     */
    private static void registerAdvisor(BeanDefinitionRegistry registry,
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
