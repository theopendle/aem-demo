package com.theopendle.core.parts.impl;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.theopendle.core.genericentities2.AbstractEntity;
import com.theopendle.core.genericentities2.table.Table;
import com.theopendle.core.parts.Part;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Named;

@Model(adaptables = Resource.class, adapters = Part.class, resourceType = PartImpl.RESOURCE_TYPE)
// TODO: Remove exporter
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PartImpl extends AbstractEntity implements Part {

    public static final String RESOURCE_TYPE = "demo/entities/part";

    @Getter
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Named(JcrConstants.JCR_DESCRIPTION)
    private String description;

    @Getter
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Named(JcrConstants.JCR_TITLE)
    private String title;

    @Override
    public String getRowResourceType() {
        return PartRowImpl.RESOURCE_TYPE;
    }

    @Override
    public Table getTable() {
        return new PartTableImpl();
    }

}
