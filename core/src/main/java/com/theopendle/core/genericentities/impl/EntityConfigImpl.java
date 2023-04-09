package com.theopendle.core.genericentities.impl;

import com.theopendle.core.genericentities.EntityConfig;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class, adapters = EntityConfig.class)
public class EntityConfigImpl implements EntityConfig {

    private static final String PN_ENTITY_RESOURCE_TYPE = "entityResourceType";

    @Self
    private SlingHttpServletRequest request;

    @Getter
    private Resource rootResource;

    @Delegate
    private Properties propertyDelegate;

    @PostConstruct
    public void init() {

        final String rootPath = request.getRequestPathInfo().getSuffix();
        if (rootPath == null) {
            log.error("Could not determine root path for entities from the request suffix <{}>",
                    request.getPathInfo());
            return;
        }

        final Resource rootResource = request.getResourceResolver().getResource(rootPath);
        if (rootResource == null) {
            log.error("There is no resource at root path <{}>", rootPath);
            return;
        }

        propertyDelegate = rootResource.adaptTo(Properties.class);

        this.rootResource = rootResource;
    }

    @Getter
    @Model(adaptables = Resource.class)
    public static class Properties {
        @ValueMapValue
        private String entityResourceType;

        @ValueMapValue
        private String createFormPath;
    }
}
