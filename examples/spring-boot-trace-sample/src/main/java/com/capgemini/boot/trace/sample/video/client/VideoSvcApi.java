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
 * Defines an API for a Video service. The interface is used to
 * provide a contract for client/server interactions. 
 */
public interface VideoSvcApi {
    
    /** the title parameter used in the REST-ful URL for finding a video by its title */ 
    String TITLE_PARAMETER = "title";

    /** The path used to access the VideoSvc controller */
    String VIDEO_SVC_PATH = "/video";

    /** The path used to access the find method on the VideoSvc controller */
    String VIDEO_TITLE_SEARCH_PATH = VIDEO_SVC_PATH + "/find";

    
    /**
     * Returns the current list of videos.  Returns an empty collection if no videos exist 
     * 
     * @return the collection of Video objects
     */
    Collection<Video> getVideoList();

    
    /**                                               
     * Adds the specified video to the list of videos.
     * 
     * @param video the Video to add to the list
     * @return true if video was added successfully, false otherwise
     */
    boolean addVideo(Video video);
    
    /**
     * Returns a collection of videos matching the specified title.
     * Returns an empty collection if no videos are matched
     * 
     * @param title the title of the video to search for
     * @return the collection of video objects
     */
    Collection<Video> findByTitle(String title);
}
