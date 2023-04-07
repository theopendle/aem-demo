package com.theopendle.core.genericentities;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// https://developer.adobe.com/experience-manager/reference-materials/6-5/granite-ui/api/jcr_root/libs/granite/ui/components/coral/foundation/table/index.html?highlight=granite%20tablecolumn
@Getter
@Builder
public class TableColumn {

    private final static String PV_LABEL_EMPTY = "Empty";
    private final static String PV_ID_EMPTY = PV_LABEL_EMPTY.toLowerCase();
    private final static String PV_ALIGNMENT_CENTER = "center";

    @JsonProperty("name")
    private String id;

    @JsonProperty(JcrConstants.JCR_TITLE)
    private String label;

    private boolean select;
    private String alignment;
    private Boolean sortable;
    private String sortType;

    public static TableColumn empty() {
        return TableColumn.builder()
                .id(PV_ID_EMPTY)
                .label(PV_LABEL_EMPTY)
                .alignment(PV_ALIGNMENT_CENTER)
                .sortable(false)
                .build();
    }

    public static TableColumn fromJsonProperty(final String name, final JsonNode jsonProperty) {
        final JsonNodeType nodeType = jsonProperty.getNodeType();

        boolean sortable = false;
        String sortType = "alphanumeric";
        switch (nodeType) {
            case NUMBER:
                sortable = true;
                sortType = "number";
                break;
            case STRING:
            case BOOLEAN:
                sortable = true;
                break;
        }

        return TableColumn.builder()
                .id(name.toLowerCase())
                .label(StringUtils.capitalize(name))
                .sortable(sortable)
                .sortType(sortType)
                .build();
    }


//    public static TableColumn fromJsonField(String fieldName, JsonNodeType fieldType) {
//        TableColumn.builder()
//                .id(fieldName.
//        case ())
//                .label(StringUtils.capitalize())
//    }

    public Resource toDataSourceResource(@NotNull final ResourceResolver resolver) {
        final Map<String, Object> propertyMap = new ObjectMapper().convertValue(this, new TypeReference<Map<String, Object>>() {
        });

        final ValueMap valueMap = new ValueMapDecorator(propertyMap);
        return new ValueMapResource(resolver, "/content/data", JcrConstants.NT_UNSTRUCTURED, valueMap);
    }
}
