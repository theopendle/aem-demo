package com.theopendle.core.genericentities.table.datasource;

import com.theopendle.core.genericentities.AbstractEntity;
import com.theopendle.core.genericentities.Entity;
import com.theopendle.core.genericentities.EntityConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;

@Slf4j
public class AbstractDataSource {

    @OSGiService
    protected ModelFactory modelFactory;

    @Self
    protected SlingHttpServletRequest request;

    @Self
    protected EntityConfig entityConfig;

    protected Entity adaptToEntityModel(final Resource resource) {

        // Get the model from the resource
        try {
            final Object model = modelFactory.getModelFromResource(resource);

            // Make sure that we are getting an entity
            if (!(model instanceof Entity)) {
                log.error("Resource at <{}> with type <{}> was adapted to model of type <{}>, not <{}>",
                        resource.getPath(), resource.getResourceType(), model.getClass(), AbstractEntity.class
                );
                return null;
            }

            return (Entity) model;

        } catch (final RuntimeException e) {
            log.error("Could not find model for resource at <{}> with type <{}>",
                    resource.getPath(), resource.getResourceType(), e);
            return null;
        }
    }
}
