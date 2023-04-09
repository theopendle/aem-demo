package com.theopendle.core.genericentities.table.datasource.impl;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.google.common.collect.ImmutableList;
import com.theopendle.core.genericentities.Entity;
import com.theopendle.core.genericentities.table.datasource.AbstractDataSource;
import com.theopendle.core.genericentities.table.datasource.RowDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = RowDataSource.class,
        resourceType = RowDataSourceImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class RowDataSourceImpl extends AbstractDataSource implements RowDataSource {
    public static final String RESOURCE_TYPE = "demo/consoles/genericentity/datasources/tableRowDataSource";

    @PostConstruct
    public void init() {

        // Get all entity resources
        final ImmutableList<Resource> resources = ImmutableList.copyOf(entityConfig.getRootResource().getChildren().iterator());

        // Adapt them to models
        final Map<Resource, Entity> resourceEntityMap = new HashMap<>();
        resources.forEach(resource -> {
            final Entity entity = adaptToEntityModel(resource);
            if (entity != null) {
                resourceEntityMap.put(resource, entity);
            }
        });

        // Create new fake resources based on the row resource type
        final Iterator<Resource> dataIterator = resourceEntityMap.entrySet()
                .stream()
                .map(entry -> (Resource) new ValueMapResource(
                        request.getResourceResolver(),
                        entry.getKey().getPath(),
                        entry.getValue().getRowResourceType(),
                        entry.getKey().getValueMap())
                )
                .iterator();

        request.setAttribute(DataSource.class.getName(), new SimpleDataSource(dataIterator));
    }
}
