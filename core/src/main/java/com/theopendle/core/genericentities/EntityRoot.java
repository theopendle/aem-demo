package com.theopendle.core.genericentities;

import org.apache.sling.api.resource.Resource;

public interface EntityRoot {

    Resource getResource();

    /**
     * @return the resource type of the generic entity. EG: "my-project/entities/my-entity"
     */
    String getEntityResourceType();

    /**
     * @return a resource type that renders a table row for an instance of the generic entity. EG: "my-project/entities/my-entity/row"
     */
    String getRowResourceType();

    /**
     * @return path to a resource representing the create form. EG: "my-project/entities/my-entity/form"
     */
    String getCreateFormPath();
}
