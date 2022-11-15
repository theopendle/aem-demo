/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.theopendle.core.models;

import com.day.cq.tagging.Tag;
import com.theopendle.core.injection.cookie.CookieValue;
import com.theopendle.core.injection.tag.PageTag;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;

import javax.annotation.PostConstruct;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class)
public class HelloWorldImpl implements HelloWorld {

    @CookieValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String marketingConsent;

    @CookieValue(cookie = "demo_session_id", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String sessionId;

    @PageTag
    private List<Tag> tags;

    @PageTag(name = "customTag")
    private Tag customTag;

    @Getter
    private String message;

    @PostConstruct
    protected void init() {
        message = "Marketing consent: " + marketingConsent + "\n"
                + "Session ID: " + sessionId + "\n"
                + "Tags: " + (tags == null ? "null" : tags.size()) + "\n"
                + "Custom tag: " + (customTag == null ? "null" : customTag.getName());
    }
}
