package com.theopendle.core.genericentities.datasource.impl;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.theopendle.core.genericentities.AbstractGenericEntity;
import com.theopendle.core.genericentities.EntityConfig;
import com.theopendle.core.genericentities.TableColumn;
import com.theopendle.core.genericentities.datasource.TableColumnDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.export.spi.ModelExporter;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.helpers.MessageFormatter;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Iterator;

@Slf4j
@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = TableColumnDataSource.class,
        resourceType = TableColumnDataSourceImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class TableColumnDataSourceImpl implements TableColumnDataSource {

    public static final String RESOURCE_TYPE = "demo/consoles/genericentity/datasources/tableColumnDataSource";

    @OSGiService
    private ModelFactory modelFactory;

    @OSGiService
    private ModelExporter modelExporter;

    @Self
    private SlingHttpServletRequest request;

    @Self
    private EntityConfig entityConfig;

    @PostConstruct
    public void init() {

        // Set fallback
        fallback();

        final Iterator<Resource> entityResourceIterator = entityConfig.getRootResource().getChildren().iterator();
        if (!entityResourceIterator.hasNext()) {
            log.warn("No entities exist under <{}>, therefore columns could not be generated dynamically",
                    entityConfig.getRootResource().getPath());
            return;
        }

        final Resource firstEntityResource = entityConfig.getRootResource().getChildren().iterator().next();

        try {
            final JsonNode jsonNode = exportModel(firstEntityResource);

            final Iterator<Resource> dataIterator = ImmutableList.copyOf(jsonNode.fieldNames())
                    .stream()
                    .filter(fieldName -> !fieldName.startsWith(":"))
                    .filter(fieldName -> !jsonNode.get(fieldName).isArray() && !jsonNode.get(fieldName).isObject())
                    .map(fieldName -> TableColumn.fromJsonProperty(fieldName, jsonNode.get(fieldName)))
                    .map(column -> column.toDataSourceResource(request.getResourceResolver()))
                    .iterator();

            request.setAttribute(DataSource.class.getName(), new SimpleDataSource(dataIterator));

        } catch (final DataSourceException e) {
            log.error("Error while building columns", e);
        }
    }

    private void fallback() {
        request.setAttribute(DataSource.class.getName(), new SimpleDataSource(
                Arrays.asList(TableColumn.empty().toDataSourceResource(request.getResourceResolver())).iterator()));
    }

    private JsonNode exportModel(final Resource resource) throws DataSourceException {
        final AbstractGenericEntity entityModel;

        // Get the model from the resource
        try {
            final Object model = modelFactory.getModelFromResource(resource);

            // Make sure that we are getting an entity
            if (!(model instanceof AbstractGenericEntity)) {
                throw new DataSourceException(String.format(
                        "Resource at <%s> with type <%s> was adapted to model of type <%s>, not <%s>",
                        resource.getPath(), resource.getResourceType(), model.getClass(), AbstractGenericEntity.class
                ));
            }

            entityModel = (AbstractGenericEntity) model;

        } catch (final RuntimeException e) {
            throw new DataSourceException(MessageFormatter.format(
                            "Could not find model for resource at <{}> with type <{}>",
                            resource.getPath(), resource.getResourceType())
                    .getMessage(), e);
        }

        return new ObjectMapper().valueToTree(entityModel);
    }
}
