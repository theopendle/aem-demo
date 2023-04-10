package com.theopendle.core.genericentities.impl;

import com.theopendle.core.genericentities.EntityRoot;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Getter
@Model(adaptables = Resource.class, adapters = EntityRoot.class)
public class EntityRootImpl implements EntityRoot {

    @Self
    private Resource resource;

    @ValueMapValue
    private String entityResourceType;

    @ValueMapValue
    private String rowResourceType;

    @ValueMapValue
    private String createFormPath;
}

