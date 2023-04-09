package com.theopendle.core.parts.impl;

import com.theopendle.core.genericentities.table.Column;
import com.theopendle.core.genericentities.table.Table;
import com.theopendle.core.genericentities.table.impl.ColumnImpl;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class PartTableImpl implements Table {

    @Getter
    protected List<Column> columns = Arrays.asList(
            new ColumnImpl("select", "Select").setSelect(true),
            new ColumnImpl("id", "ID", Column.SORT_TYPE_ALPHANUMERIC),
            new ColumnImpl("title", "Title", Column.SORT_TYPE_ALPHANUMERIC),
            new ColumnImpl("description", "Description", Column.SORT_TYPE_ALPHANUMERIC)
    );
}
