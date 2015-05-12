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
package com.capgemini.boot.trace.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Trace Logger configuration backed by properties.
 * 
 * Pointcuts are configured via trace-logging.pointcut.[name] properties.
 */
@Component
@ConfigurationProperties(prefix = TraceLoggerSettings.SETTINGS_PREFIX)
public final class TraceLoggerSettings {
    
    private static final String DEFAULT_ENTER_MESSAGE = "Entering $[methodName]($[arguments])";
    
    private static final String DEFAULT_EXIT_MESSAGE = "Leaving  $[methodName](), returned $[returnValue]";
    
    private static final String DEFAULT_EXCEPTION_MESSAGE = "Exception when executing $[methodName](): $[exception]";
    
    public static final String SETTINGS_PREFIX = "trace-logging";
    
    public static final String ENABLED_SETTING = "enabled";

    private Map<String, String> pointcut = new HashMap<String, String>();
    
    private Message message = new Message();
    
    private boolean enabled = true;

    public Map<String, String> getPointcut() {
        return pointcut;
    }
    
    public Message getMessage() {
        return message;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean getEnabled() {
        return enabled;
    }
    
    public static class Message {
        private String enter = DEFAULT_ENTER_MESSAGE;
        
        private String exit = DEFAULT_EXIT_MESSAGE;
        
        private String exception = DEFAULT_EXCEPTION_MESSAGE;

        public String getEnter() {
            return enter;
        }

        public void setEnter(String enter) {
            this.enter = enter;
        }

        public String getExit() {
            return exit;
        }

        public void setExit(String exit) {
            this.exit = exit;
        }

        public String getException() {
            return exception;
        }

        public void setException(String exception) {
            this.exception = exception;
        }        
    }
}
