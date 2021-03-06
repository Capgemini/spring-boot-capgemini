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

import com.capgemini.boot.trace.annotation.EnableTraceLogger;
import com.capgemini.boot.trace.annotation.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@EnableTraceLogger
public class TestApplication {
    @Autowired
    private Foo foo;
    
    @Autowired
    private ClassLevelTrace classLevelTrace;

    @RequestMapping("/")
    String home() {
        return foo.getMessageWithAnnotation();
    }

    @RequestMapping("/nonAnnotated")
    String nonAnnotated() {
        return foo.getMessageWithoutAnnotation();
    }

    @RequestMapping("/anotherNonAnnotated")
    String anotherNonAnnotated() {
        return foo.getAnotherMessageWithoutAnnotation();
    }
    
    @RequestMapping("/classAnnotated")
    String classLevelAnnotation() {
        return classLevelTrace.getMessage();
    }
    
    @RequestMapping("/exception")
    String exception() {
        return foo.getMessageWithAnnotationThrowingException();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestApplication.class, args);
    }

    @Component
    public class Foo {
        @Autowired
        public Foo() {
        }

        @Trace
        public String getMessageWithAnnotation() {
            return "Hello World!";
        }
        
        @Trace
        public String getMessageWithAnnotationThrowingException() {
            throw new RuntimeException();
        }

        public String getMessageWithoutAnnotation() {
            return "Hello Non Annotated World!";
        }

        public String getAnotherMessageWithoutAnnotation() {
            return "Hello Non Annotated Universe!";
        }
    }
    
    @Component
    @Trace
    public class ClassLevelTrace {
        @Autowired
        public ClassLevelTrace() {
            
        }
        
        public String getMessage() {
            return "Hello World!";
        }
    }
}
