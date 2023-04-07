package com.theopendle.core.genericentities.table;

import com.theopendle.core.genericentities.table.impl.TableCellImpl;

import java.util.List;

public interface TableRow {
    List<TableCellImpl> getCells();
}
