package com.theopendle.core.genericentities.table;

public interface Column {

    String SORT_TYPE_ALPHANUMERIC = "aplhpanumeric";
    String SORT_TYPE_DATE = "date";
    String SORT_TYPE_NUMBER = "number";

    String getName();

    boolean isSelect();

    boolean isOrder();

    String getTitle();

    String getAlignment();

    boolean isFixedWidth();

    boolean isDraggable();

    boolean isHidden();

    boolean isSortable();

    String getSortType();
}
