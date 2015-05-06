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
package com.capgemini.boot.trace.sample.video.controller;

import com.capgemini.boot.trace.sample.video.client.VideoSvcApi;
import com.capgemini.boot.trace.sample.video.repository.Video;
import com.capgemini.boot.trace.sample.video.repository.VideoRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * This simple VideoSvc allows clients to send HTTP POST requests with videos
 * that are stored in memory using a list. Clients can send HTTP GET requests to
 * receive a JSON listing of the videos that have been sent to the controller so
 * far. Stopping the controller will cause it to lose the history of videos that
 * have been sent to it because they are stored in memory.
 * 
 * Notice how much simpler this VideoSvc is than the original VideoServlet?
 * Spring allows us to dramatically simplify our service. Another important
 * aspect of this version is that we have defined a VideoSvcApi that provides
 * strong typing on both the client and service interface to ensure that we
 * don't send the wrong paraemters, etc.
 */

// Tell Spring that this class is a Controller that should
// handle certain HTTP requests for the DispatcherServlet
@RestController
public class VideoSvc implements VideoSvcApi {
    // The VideoRepository that we are going to store our videos
    // in. We don't explicitly construct a VideoRepository, but
    // instead mark this object as a dependency that needs to be
    // injected by Spring. Our Application class has a method
    // annotated with @Bean that determines what object will end
    // up being injected into this member variable.
    //
    // Also notice that we don't even need a setter for Spring to
    // do the injection.
    //
    @Autowired
    private VideoRepository videos;

    // Receives POST requests to /video and converts the HTTP
    // request body, which should contain json, into a Video
    // object before adding it to the list. The @RequestBody
    // annotation on the Video parameter is what tells Spring
    // to interpret the HTTP request body as JSON and convert
    // it into a Video object to pass into the method. The
    // @ResponseBody annotation tells Spring to conver the
    // return value from the method back into JSON and put
    // it into the body of the HTTP response to the client.
    //
    // The VIDEO_SVC_PATH is set to "/video" in the VideoSvcApi
    // interface. We use this constant to ensure that the
    // client and service paths for the VideoSvc are always
    // in synch.
    //
    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.POST)
    public @ResponseBody boolean addVideo(@RequestBody Video v) {
        videos.save(v);
        return true;
    }

    // Receives GET requests to /video and returns the current
    // list of videos in memory. Spring automatically converts
    // the list of videos to JSON because of the @ResponseBody
    // annotation.
    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Video> getVideoList() {
        return Lists.newArrayList(videos.findAll());
    }

    // Receives GET requests to /video/find and returns all Videos
    // that have a title (e.g., Video.name) matching the "title" request
    // parameter value that is passed by the client
    @RequestMapping(value = VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Video> findByTitle(
    // Tell Spring to use the "title" parameter in the HTTP request's query
    // string as the value for the title method parameter
            @RequestParam(TITLE_PARAMETER) String title) {
        return videos.findByName(title);
    }

}
