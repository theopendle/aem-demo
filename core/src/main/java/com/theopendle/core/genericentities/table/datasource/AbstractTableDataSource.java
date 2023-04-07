package com.theopendle.core.genericentities.table.datasource;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.google.common.collect.ImmutableList;
import com.theopendle.core.genericentities.AbstractGenericEntity;
import com.theopendle.core.genericentities.EntityConfig;
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
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class AbstractTableDataSource {

    @OSGiService
    protected ModelFactory modelFactory;

    @OSGiService
    protected ModelExporter modelExporter;

    @Self
    protected SlingHttpServletRequest request;

    @Self
    protected EntityConfig entityConfig;

    public static Map<String, ValueNode> getValidValues(final ObjectNode objectNode) {
        return ImmutableList.copyOf(objectNode.fieldNames())
                .stream()
                .filter(fieldName -> !fieldName.startsWith(":"))
                .filter(fieldName -> !objectNode.get(fieldName).isArray() && !objectNode.get(fieldName).isObject())
                .collect(Collectors.toMap(Function.identity(), fieldName -> (ValueNode) objectNode.get(fieldName)));
    }

    protected List<ObjectNode> getEntityResourcesAsJsonNodes() {
        return ImmutableList.copyOf(entityConfig.getRootResource().getChildren().iterator())
                .stream()
                .map(this::exportModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected ObjectNode exportModel(final Resource resource) {
        final AbstractGenericEntity entityModel;

        // Get the model from the resource
        try {
            final Object model = modelFactory.getModelFromResource(resource);

            // Make sure that we are getting an entity
            if (!(model instanceof AbstractGenericEntity)) {
                log.error("Resource at <{}> with type <{}> was adapted to model of type <{}>, not <{}>",
                        resource.getPath(), resource.getResourceType(), model.getClass(), AbstractGenericEntity.class
                );
                return null;
            }

            entityModel = (AbstractGenericEntity) model;

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

//                .peek(propertyMap -> propertyMap.put("sling:resourceType", "demo/consoles/genericentity/table/components/row"))

                // Convert to value map
                .map(ValueMapDecorator::new)

                // Build fake resource
                // TODO: Use something else as the path
                .map(valueMap -> (Resource) new ValueMapResource(
                        request.getResourceResolver(), "/content/data", JcrConstants.NT_UNSTRUCTURED, valueMap))
                .iterator();

        request.setAttribute(DataSource.class.getName(), new SimpleDataSource(dataIterator));
    }
}
