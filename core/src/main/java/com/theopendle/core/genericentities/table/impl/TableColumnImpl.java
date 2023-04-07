package com.theopendle.core.genericentities.table.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ValueNode;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

// https://developer.adobe.com/experience-manager/reference-materials/6-5/granite-ui/api/jcr_root/libs/granite/ui/components/coral/foundation/table/index.html?highlight=granite%20tablecolumn
@Getter
@Builder
public class TableColumnImpl {

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

    public static TableColumnImpl empty() {
        return TableColumnImpl.builder()
                .id(PV_ID_EMPTY)
                .label(PV_LABEL_EMPTY)
                .alignment(PV_ALIGNMENT_CENTER)
                .sortable(false)
                .build();
    }

    public static TableColumnImpl fromValueNodeMap(final String name, final ValueNode valueNode) {
        final JsonNodeType nodeType = valueNode.getNodeType();

        // Define sorting behaviour
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

        return TableColumnImpl.builder()
                .id(name.toLowerCase())
                .label(StringUtils.capitalize(name))
                .sortable(sortable)
                .sortType(sortType)
                .build();
    }
}
