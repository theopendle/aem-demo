package com.theopendle.core.models;

import com.adobe.granite.ui.components.ExpressionHelper;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.*;

import javax.annotation.PostConstruct;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class)
public class HasTitle {

    @OSGiService
    private ExpressionResolver expressionResolver;

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private PageManager pageManager;

    @ValueMapValue
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String pagePathExpression;

    @PostConstruct
    public void init() {

        if (pagePathExpression == null) {
            log.error("Render condition failed. Please provide a <pagePathExpression> property");
            return;
        }

        final String pagePath = new ExpressionHelper(expressionResolver, request).getString(pagePathExpression);
        if (pagePath == null) {
            log.error("Could not resolve expression <{}> to a page path", pagePathExpression);
            return;
        }

        final Page page = pageManager.getPage(pagePath);
        if (page == null) {
            log.error("Could not find page at path <{}>", pagePath);
            return;
        }

        // Show only if the page title is "Demo"
        final boolean show = "Demo".equals(page.getTitle());

        // Add the render condition
        request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(show));
    }
}