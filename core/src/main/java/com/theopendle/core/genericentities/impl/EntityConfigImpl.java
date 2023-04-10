package com.theopendle.core.genericentities.impl;

import com.theopendle.core.genericentities.EntityConfig;
import com.theopendle.core.genericentities.EntityRoot;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.helpers.MessageFormatter;

import javax.annotation.PostConstruct;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class, adapters = EntityConfig.class)
public class EntityConfigImpl implements EntityConfig {
    @Self
    private SlingHttpServletRequest request;

    @Getter
    private Resource resource;

    @Delegate(excludes = DelegationExclusion.class)
    private EntityRoot entityRoot;

    @PostConstruct
    public void init() {

        final String path = request.getRequestPathInfo().getSuffix();
        if (path == null) {
            log.error("Could not determine entity path from the request suffix <{}>",
                    request.getPathInfo());
            return;
        }

        resource = request.getResourceResolver().getResource(path);
        if (resource == null) {
            log.error("There is no resource at path <{}>", path);
            return;
        }

        entityRoot = findEntityRoot(resource);
    }

    @Override
    public Resource getRootResource() {
        return entityRoot.getResource();
    }

    private static EntityRoot findEntityRoot(final Resource resource) {

        final String PN_ENTITY_ROOT = "entityRoot";

        if (resource.getValueMap().get(PN_ENTITY_ROOT, false)) {
            final EntityRoot root = resource.adaptTo(EntityRoot.class);
            if (root == null) {
                throw new IllegalStateException(MessageFormatter.format(
                        "Found entity root at <{}> but could not adapt to <{}>",
                        resource.getPath(), EntityRoot.class).getMessage());
            }
            return root;
        }

        if (resource.getParent() == null) {
            throw new IllegalStateException("Could not find entity root");
        }

        return findEntityRoot(resource.getParent());
    }

    private interface DelegationExclusion {
        Resource getResource();
    }
}
