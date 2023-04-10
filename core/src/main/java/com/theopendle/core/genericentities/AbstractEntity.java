package com.theopendle.core.genericentities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

@JsonPropertyOrder({"name"})
public abstract class AbstractEntity extends AbstractExportedModel implements Entity {

    @Getter
    @JsonIgnore
    @Self
    private Resource resource;

    @Override
    @JsonIgnore
    public String getPath() {
        return resource.getPath();
    }

    @Override
    public String getName() {
        return resource.getName();
    }

    @Override
    public int compareTo(@NotNull final Entity entity) {
        return getName().compareTo(entity.getName());
    }
}
