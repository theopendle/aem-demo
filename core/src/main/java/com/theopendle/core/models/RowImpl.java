package com.theopendle.core.models;

import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = Row.class,
        resourceType = RowImpl.RESOURCE_TYPE
)
public class RowImpl implements Row {

    public static final String RESOURCE_TYPE = "demo/componnents/row";

    @Delegate
    @Self
    @Via(type = ResourceSuperType.class)
    private LayoutContainer layoutContainer;

    @Getter
    @ValueMapValue
    private String backgroundColor;
}
