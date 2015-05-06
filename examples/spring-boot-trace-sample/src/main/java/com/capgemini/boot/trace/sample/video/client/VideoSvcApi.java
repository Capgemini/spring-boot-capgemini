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
package com.capgemini.boot.trace.sample.video.client;

import com.capgemini.boot.trace.sample.video.repository.Video;

import java.util.Collection;

/**
 * This interface defines an API for a VideoSvc. The interface is used to
 * provide a contract for client/server interactions. 
 */
public interface VideoSvcApi {
    String TITLE_PARAMETER = "title";

    // The path where we expect the VideoSvc to live
    String VIDEO_SVC_PATH = "/video";

    // The path to search videos by title
    String VIDEO_TITLE_SEARCH_PATH = VIDEO_SVC_PATH + "/find";

    Collection<Video> getVideoList();
    
    boolean addVideo(Video v);
    
    Collection<Video> findByTitle(String title);
}
