package com.theopendle.core.genericentities;

import org.apache.sling.api.resource.Resource;

public interface EntityConfig extends EntityRoot {

    @Override
    Resource getResource();

    Resource getRootResource();
}
