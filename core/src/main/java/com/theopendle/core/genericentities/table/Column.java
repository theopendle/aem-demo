package com.theopendle.core.genericentities.table;

public interface Column {
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
