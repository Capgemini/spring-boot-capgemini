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

package com.capgemini.boot.core.factory;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * Strategy for the post processor, providing the logic for establishing if a
 * class/method is a setting backed factory class/method, and obtaining the
 * the configuration for these factories.
 * 
 */
public interface FactoryProcessorStrategy {  
    
    /**
     * Get all setting backed factory beans in the application.
     * 
     * @param context The application context
     * @return The names of all setting backed factory beans
     */
    String[] getFactoryBeanNames(ApplicationContext context);
    
    /**
     * Establish if a method is a setting backed bean factory method.
     * 
     * @param method The method
     * @return True if the method is a setting backed bean factory method, false otherwise
     */
    boolean isFactoryMethod(Method method);
    
    /**
     * Gets the class that defines the settings for specified the setting backed bean factory.
     * 
     * @param factoryClass The setting backed bean factory class
     * @return The settings class
     */
    Class<?> getSettingsClass(Class<?> factoryClass);
    
    /**
     * Gets the setting prefix for the specified factory class.
     * 
     * @param factoryClass The setting backed bean factory class
     * @return The setting prefix
     */
    String getSettingPrefix(Class<?> factoryClass);
    
    /**
     * The suffix that should be appending to the bean name for beans 
     * created via the specified factory method.
     * 
     * @param factoryMethod The factory method
     * @return The bean name suffix
     */
    String getBeanNameSuffix(Method factoryMethod);
    
    /**
     * The setting name that beans created via the specified factory method should be backed by.
     * 
     * This should correspond to a getter method in the settings class.
     * 
     * @param factoryMethod The factory method
     * @return The backed setting name.
     */
    String getSettingName(Method factoryMethod);
}
