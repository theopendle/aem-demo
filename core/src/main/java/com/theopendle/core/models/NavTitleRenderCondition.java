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

import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class)
public class NavTitleRenderCondition {

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private PageManager pageManager;

    @ValueMapValue
    private String allowedNavTitle;

    @PostConstruct
    public void init() {

        // Get the resource path
        // eg: "/content/demo/us/en/test-page/jcr:content/root/responsivegrid/demo"
        final String suffix = request.getRequestPathInfo().getSuffix();
        if (suffix == null) {
            throw new IllegalArgumentException("Could not determine page from <" + request.getPathInfo() + ">");
        }

        // Keep only the page path
        // eg: "/content/demo/us/en/test-page"
        final String[] split = suffix.split("/jcr:content");
        if (split.length < 1) {
            throw new IllegalArgumentException("Could not determine page from <" + request.getPathInfo() + ">");
        }

        // Find the page
        final String pagePath = split[0];
        final Page page = pageManager.getPage(pagePath);
        if (page == null) {
            throw new IllegalArgumentException("Resource at <" + pagePath + "> is not a page");
        }

        // Check if we have the right nav title
        final boolean show = allowedNavTitle.equals(page.getNavigationTitle());

        // Add the render condition
        request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(show));
    }
}
