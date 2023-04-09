package com.theopendle.core.genericentities2.table.datasource;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.theopendle.core.genericentities.EntityConfig;
import com.theopendle.core.genericentities2.AbstractEntity;
import com.theopendle.core.genericentities2.Entity;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.export.spi.ModelExporter;
import org.apache.sling.models.factory.ModelFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class AbstractDataSource {

    @OSGiService
    protected ModelFactory modelFactory;

    @OSGiService
    protected ModelExporter modelExporter;

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

    protected ObjectNode exportModel(final Resource resource) {
        final AbstractEntity entityModel;

        // Get the model from the resource
        try {
            final Object model = modelFactory.getModelFromResource(resource);

            // Make sure that we are getting an entity
            if (!(model instanceof AbstractEntity)) {
                log.error("Resource at <{}> with type <{}> was adapted to model of type <{}>, not <{}>",
                        resource.getPath(), resource.getResourceType(), model.getClass(), AbstractEntity.class
                );
                return null;
            }

            entityModel = (AbstractEntity) model;

        } catch (final RuntimeException e) {
            log.error("Could not find model for resource at <{}> with type <{}>",
                    resource.getPath(), resource.getResourceType(), e);
            return null;
        }

        return new ObjectMapper().valueToTree(entityModel);
    }

    protected <T> void injectDataIntoRequest(final List<T> data) {
        final Iterator<Resource> dataIterator = data.stream()
                // Get property map
                .map(object -> new ObjectMapper().convertValue(object, new TypeReference<Map<String, Object>>() {
                }))


                // Convert to value map
                .map(ValueMapDecorator::new)

                // Build fake resource
                // TODO: Use something else as the path
                .map(valueMap -> (Resource) new ValueMapResource(
                        request.getResourceResolver(),
                        entityConfig.getRootResource().getPath(),
                        "demo/consoles/genericentity/table/components/row",//entityConfig.getRowResourceType(),
                        valueMap))
                .iterator();

        request.setAttribute(DataSource.class.getName(), new SimpleDataSource(dataIterator));
    }
}
