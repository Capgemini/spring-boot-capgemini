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

package com.capgemini.boot.core.factory.internal;

import java.lang.reflect.Method;

import org.springframework.beans.factory.BeanCreationException;

public class DefaultExceptionFactory implements ExceptionFactory {

    private static final String INVALID_ARGUMENTS = "The arguments for factory method %s are incorrect. "
            + "The first argument should always be the type of the linked setting and a second optional "
            + "argument is allowed, with the same type as the specified settings class.";
    
    private static final String NO_MATCHING_SETTING = "A getter for the setting named %s does not exist";
    
    private static final String ILLEGAL_SETTING_ACCESS = "Getter method for the setting named %s must be public";
    
    private static final String ILLEGAL_SETTING_ARGUMENTS = "Getter method for the setting names %s should not have any arguments";
    @Override
    public RuntimeException createInvalidArgumentsException(Method method) {
        final String message = String.format(INVALID_ARGUMENTS, method.getName());
        
        return createGenericBeanCreationException(message);
    }
    @Override
    public RuntimeException createNoMatchingSettingException(String property) {
        final String message = String.format(NO_MATCHING_SETTING, property);
        
        return createGenericBeanCreationException(message);
    }
    
    @Override
    public RuntimeException createSettingAccessException(String property) {
        final String message = String.format(ILLEGAL_SETTING_ACCESS, property);
        
        return createGenericBeanCreationException(message);
    }
    
    @Override
    public RuntimeException createSettingArgumentsException(String property) {
        final String message  = String.format(ILLEGAL_SETTING_ARGUMENTS, property);
        
        return createGenericBeanCreationException(message);
    }
    
    @Override
    public RuntimeException createGenericException(String message, Throwable cause) {
        return createGenericBeanCreationException(message, cause);
    }
    
    private RuntimeException createGenericBeanCreationException(String message) {
        return createGenericBeanCreationException(message, null);
    }
    
    private RuntimeException createGenericBeanCreationException(String message, Throwable cause) {
        return new BeanCreationException(message);
    }
}
