package com.theopendle.core.genericentities2.table.datasource.impl;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.theopendle.core.genericentities2.Entity;
import com.theopendle.core.genericentities2.table.Column;
import com.theopendle.core.genericentities2.table.datasource.AbstractDataSource;
import com.theopendle.core.genericentities2.table.datasource.ColumnDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = ColumnDataSource.class,
        resourceType = ColumnDataSourceImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ColumnDataSourceImpl extends AbstractDataSource implements ColumnDataSource {

    public static final String RESOURCE_TYPE = "demo/consoles/genericentity/datasources/tableColumnDataSource";

    @PostConstruct
    public void init() {

        // Get all entity resources
        final ImmutableList<Resource> resources = ImmutableList.copyOf(entityConfig.getRootResource().getChildren().iterator());

        if (resources.isEmpty()) {
            log.warn("No entities exist under <{}>, therefore columns could not be generated dynamically",
                    entityConfig.getRootResource().getPath());
            return;
        }
        final Resource firstResource = resources.get(0);
        final Entity entity = adaptToEntityModel(firstResource);
        if (entity == null) {
            log.error("Error while building columns. Resource at <{}> cannot be adapted to a Sling model.", firstResource.getPath());
            return;
        }

        final List<Column> columns = entity.getTable().getColumns();

        final Iterator<Resource> dataIterator = columns.stream()

                // Get column properties as a map
                .map(object -> new ObjectMapper().convertValue(object, new TypeReference<Map<String, Object>>() {
                }))

                // Convert to value map
                .map(ValueMapDecorator::new)

                // Build fake resource
                .map(valueMap -> (Resource) new ValueMapResource(
                        request.getResourceResolver(),
                        entityConfig.getRootResource().getPath(),
                        entityConfig.getRootResource().getResourceType(),
                        valueMap))
                .iterator();

        request.setAttribute(DataSource.class.getName(), new SimpleDataSource(dataIterator));
    }
}
