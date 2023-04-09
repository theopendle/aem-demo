package com.theopendle.core.genericentities.table.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.theopendle.core.genericentities.table.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ColumnImpl implements Column {
    @JsonProperty(JcrConstants.JCR_TITLE)
    private String title;

    private String name;
    private boolean select;
    private boolean order;
    private String alignment;
    private boolean fixedWidth;
    private boolean draggable;
    private boolean hidden;
    private String sortType;

    public ColumnImpl(final String name, final String title) {
        this.name = name;
        this.title = title;
    }

    public ColumnImpl(final String name, final String title, final String sortType) {
        this.name = name;
        this.title = title;
        this.sortType = sortType;
    }

    @Override
    public boolean isSortable() {
        return StringUtils.isNotBlank(sortType);
    }
}
