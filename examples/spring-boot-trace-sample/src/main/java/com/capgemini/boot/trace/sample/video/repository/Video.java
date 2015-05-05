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
package com.capgemini.boot.trace.sample.video.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.capgemini.boot.trace.annotation.Trace;
import com.google.common.base.Objects;

/**
 * A simple object to represent a video and its URL for viewing.
 * 
 * @author jules
 * 
 */
@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String url;
    private long duration;

    public Video() {
    }

    public Video(String name, String url, long duration) {
        super();
        this.name = name;
        this.url = url;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Two Videos will generate the same hashcode if they have exactly the same
     * values for their name, url, and duration.
     * 
     */
    @Override
    public int hashCode() {
        // Google Guava provides great utilities for hashing
        return Objects.hashCode(name, url, duration);
    }

    /**
     * Two Videos are considered equal if they have exactly the same values for
     * their name, url, and duration.
     * 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Video) {
            Video other = (Video) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(name, other.name)
                    && Objects.equal(url, other.url)
                    && duration == other.duration;
        } else {
            return false;
        }
    }

}
