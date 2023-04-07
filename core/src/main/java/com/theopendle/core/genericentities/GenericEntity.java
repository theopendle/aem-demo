package com.theopendle.core.genericentities;

import com.adobe.cq.export.json.ComponentExporter;
import org.apache.sling.api.resource.Resource;

public interface GenericEntity extends ComponentExporter {
    
    Resource getResource();

    String getPath();

    String getName();
}
