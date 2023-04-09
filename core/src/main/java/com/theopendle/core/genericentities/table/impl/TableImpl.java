package com.theopendle.core.genericentities.table.impl;


import com.theopendle.core.genericentities.table.Column;
import com.theopendle.core.genericentities.table.Table;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class TableImpl implements Table {

    @Getter
    protected List<Column> columns = Arrays.asList(
            new ColumnImpl("select", "Select").setSelect(true),
            new ColumnImpl("name", "Name"),
            new ColumnImpl("title", "Title")
    );

}
