package com.theopendle.core.parts;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.theopendle.core.genericentities.AbstractGenericEntity;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, resourceType = "demo/components/entities/part")
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class Part extends AbstractGenericEntity {

    @Getter
    @ValueMapValue(name = JcrConstants.JCR_DESCRIPTION)
    private String description;
}
