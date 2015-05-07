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

import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import com.capgemini.boot.core.factory.SettingBackedBean;
import com.capgemini.boot.core.factory.SettingBackedBeanFactory;
import com.capgemini.boot.trace.settings.TraceLoggerSettings;

/**
 * Configures trace logging for enabled spring-boot applications for configured
 * pointcuts.
 * 
 * Pointcuts are configured via trace-logging.pointcut.[name] properties.
 */
@SettingBackedBeanFactory(settingsClass = TraceLoggerSettings.class, settingPrefix = TraceLoggerSettings.SETTINGS_PREFIX)
public class TraceLoggerRegistrar {
    
    private List<String> previouslyAddedTracePointcuts = new ArrayList<String>();

    @SettingBackedBean(setting = "pointcut", beanNameSuffix = "Advisor")
    public Advisor createAdvisor(String pointcutExpression, TraceLoggerSettings settings) {
        Advisor advisor = null;
        
        if (settings.getEnabled()) {
            final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression(generateAdvisorPointcutExpression(pointcutExpression));

            advisor = new DefaultPointcutAdvisor(pointcut, TraceLoggerConfigurationUtils.createTraceInterceptor(settings));
        }

        return advisor;
    }
    
    private String generateAdvisorPointcutExpression(String basePointcutExpression) {
        
        String pointcut = basePointcutExpression 
                + " and not (" 
                + TraceLoggerConfiguration.TRACE_ANNOTATION + ")";
        
        //Add previously added pointcuts as 'not' clauses in this pointcut so that trace logging
        //does not potentially occur multiple times per method invocation
        for (String previousPointcut : previouslyAddedTracePointcuts) {
            pointcut = pointcut + " and not (" + previousPointcut + ")";
        }
        
        previouslyAddedTracePointcuts.add(basePointcutExpression);
        
        return pointcut;
    }
}
