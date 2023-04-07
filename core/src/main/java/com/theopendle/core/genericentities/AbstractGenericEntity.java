package com.theopendle.core.genericentities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.Self;

@JsonPropertyOrder({"name"})
public abstract class AbstractGenericEntity extends AbstractExportedModel {

    @Getter
    @JsonIgnore
    @Self
    private Resource resource;

    @JsonIgnore
    public String getPath() {
        return resource.getPath();
    }

    public String getName() {
        return resource.getName();
    }
}
