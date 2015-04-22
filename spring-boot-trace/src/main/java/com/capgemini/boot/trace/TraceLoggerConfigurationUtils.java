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

import org.springframework.aop.interceptor.AbstractTraceInterceptor;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;

import com.capgemini.boot.trace.settings.TraceLoggerSettings;

/**
 * Utility methods for trace logger configuration.
 *
 */
public final class TraceLoggerConfigurationUtils {

    /**
     * Private constructor as this class provides static methods only
     */
    private TraceLoggerConfigurationUtils() { }

    /**
     * Creates a trace interceptor for logging entry into and exit from methods.
     * 
     * @return The created trace interceptor
     */
    public static AbstractTraceInterceptor createTraceInterceptor(TraceLoggerSettings settings) {
        CustomizableTraceInterceptor customizableTraceInterceptor = new CustomizableTraceInterceptor();
        customizableTraceInterceptor.setUseDynamicLogger(true);
        customizableTraceInterceptor.setEnterMessage(settings.getMessage().getEnter());
        customizableTraceInterceptor.setExitMessage(settings.getMessage().getExit());
        customizableTraceInterceptor.setExceptionMessage(settings.getMessage().getException());
        return customizableTraceInterceptor;
    }
}
