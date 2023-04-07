package com.theopendle.core.genericentities;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class)
public class Item {

    @Self
    private Resource resource;
    
    @PostConstruct
    public void init() {

    }
}
