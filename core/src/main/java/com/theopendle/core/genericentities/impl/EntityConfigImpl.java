package com.theopendle.core.genericentities.impl;

import com.theopendle.core.genericentities.EntityConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class, adapters = EntityConfig.class)
public class EntityConfigImpl implements EntityConfig {

    private static final String PN_ENTITY_RESOURCE_TYPE = "entityResourceType";

    @Self
    private SlingHttpServletRequest request;

    @Getter
    private Resource rootResource;

    @Getter
    private String entityResourceType;

    @Getter
    private String rowResourceType;

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

        final String entityResourceType = rootResource.getValueMap().get(PN_ENTITY_RESOURCE_TYPE, String.class);
        if (entityResourceType == null) {
            log.error("Root resource at path <{}> does not specify <{}> property", rootPath, PN_ENTITY_RESOURCE_TYPE);
            return;
        }

        this.rootResource = rootResource;
        this.entityResourceType = entityResourceType;
        this.rowResourceType = entityResourceType + "/row";
    }

}
