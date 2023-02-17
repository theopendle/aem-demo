package com.theopendle.core.models;

import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Slf4j
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
            log.error("Could not determine page from <{}>", request.getPathInfo());
            return;
        }

        // Keep only the page path
        // eg: "/content/demo/us/en/test-page"
        final String[] split = suffix.split("/jcr:content");
        if (split.length < 1) {
            log.error("Could not determine page from <{}>", request.getPathInfo());
            return;
        }

        // Find the page
        final String pagePath = split[0];
        final Page page = pageManager.getPage(pagePath);
        if (page == null) {
            log.error("Resource at <{}> is not a page", pagePath);
            return;
        }

        // Check if we have the right nav title
        final boolean show = allowedNavTitle.equals(page.getNavigationTitle());

        // Add the render condition
        request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(show));
    }
}