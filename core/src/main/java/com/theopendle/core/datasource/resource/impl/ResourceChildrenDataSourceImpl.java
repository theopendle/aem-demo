package com.theopendle.core.datasource.resource.impl;

import com.theopendle.core.datasource.resource.ResourceChildrenDataSource;
import com.theopendle.core.datasource.resource.WidgetDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
        adapters = ResourceChildrenDataSource.class,
        resourceType = ResourceChildrenDataSourceImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResourceChildrenDataSourceImpl implements ResourceChildrenDataSource {

    public static final String RESOURCE_TYPE = "jss-foundation/datasources/resourceChildrenDataSource";

    public static final String NN_DATA_SOURCE = "datasource";

    @OSGiService
    private ModelFactory modelFactory;

    @Getter
    @Self
    private SlingHttpServletRequest request;

    @Getter
    private List<Resource> data;

    @PostConstruct
    public void init() {

//        // Set fallback
//        request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
//
//        // Find the widget datasource node
//        final Resource widgetDataSourceResource = request.getResource().getChild(NN_DATA_SOURCE);
//        if (widgetDataSourceResource == null) {
//            log.error("Received request to provide data source from <{}> but resource does not have a <{}> child",
//                    request.getResource(), NN_DATA_SOURCE);
//            return;
//        }
//
//        final WidgetDataSource widgetDataSource = widgetDataSourceResource.adaptTo(WidgetDataSource.class);
//        if (widgetDataSource == null) {
//            log.error("Could not adapt resource <{}> to <{}>. Defaulting to empty data source",
//                    widgetDataSourceResource.getPath(), WidgetDataSource.class);
//            return;
//        }
//
//        // Find parent resource
//        final Resource parentResource = request.getResourceResolver().getResource(widgetDataSource.getParentResourcePath());
//        if (parentResource == null) {
//            log.error("Could not find parent resource at <{}>", widgetDataSource.getParentResourcePath());
//            return;
//        }
//
//        // Make data available without request
//        data = new ArrayList<>();
//        parentResource.listChildren().forEachRemaining(data::add);
//
//        // Feed children into data source
//        final DataSource dataSource = new SimpleDataSource(data.iterator());
//        request.setAttribute(DataSource.class.getName(), dataSource);
    }

    @Model(adaptables = Resource.class,
            adapters = WidgetDataSource.class)
    public static class WidgetDataSourceImpl implements WidgetDataSource {
        @Getter
        @ValueMapValue
        private String parentResourcePath;
    }
}
