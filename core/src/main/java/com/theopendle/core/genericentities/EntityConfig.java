package com.theopendle.core.genericentities;

import org.apache.sling.api.resource.Resource;

public interface EntityConfig {

    Resource getRootResource();

    /**
     * @return the resource type of the generic entity. EG: "my-project/entities/my-entity"
     */
    String getEntityResourceType();
}