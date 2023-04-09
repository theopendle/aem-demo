package com.theopendle.core.parts.impl;

import com.adobe.cq.export.json.ExporterConstants;
import com.theopendle.core.parts.Part;
import com.theopendle.core.parts.PartRow;
import lombok.experimental.Delegate;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = Resource.class, adapters = PartRow.class, resourceType = PartRowImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PartRowImpl implements PartRow {

    public static final String RESOURCE_TYPE = "demo/entities/part/row";

    @Delegate(excludes = DelegateExclusion.class)
    @Self
    private Part entity;

    interface DelegateExclusion {
        PartRowImpl getRow();
    }
}
