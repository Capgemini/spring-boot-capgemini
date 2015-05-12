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
package com.capgemini.boot.trace.sample.video;

import com.capgemini.boot.trace.annotation.EnableTraceLogger;
import com.capgemini.boot.trace.sample.video.repository.VideoRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


// Tell Spring to automatically create a JPA implementation of VideoRepository
@EnableJpaRepositories(basePackageClasses = VideoRepository.class)
// This is a convenience annotation that is equivalent to declaring @Configuration, @EnableAutoConfiguration and @ComponentScan.
@SpringBootApplication
// Enable configurable logging of method entry/exit points
@EnableTraceLogger
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
