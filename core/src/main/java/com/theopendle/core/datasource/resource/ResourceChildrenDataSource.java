package com.theopendle.core.datasource.resource;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.List;

public interface ResourceChildrenDataSource {

    SlingHttpServletRequest getRequest();

    List<Resource> getData();
}
