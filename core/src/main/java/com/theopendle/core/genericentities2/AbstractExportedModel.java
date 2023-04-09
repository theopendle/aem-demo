package com.theopendle.core.genericentities2;

import com.adobe.cq.export.json.ComponentExporter;
import com.drew.lang.annotations.NotNull;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.Self;

public abstract class AbstractExportedModel implements ComponentExporter {

    @Self
    private Resource resource;

    @Override
    public @NotNull String getExportedType() {
        return this.resource.getResourceType();
    }

}
