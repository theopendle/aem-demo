package com.theopendle.core.genericentities.table;

import com.theopendle.core.genericentities.Entity;
import org.jetbrains.annotations.NotNull;

public interface Row extends Comparable<Row> {

    Entity getEntity();

    String getPath();

    String getName();

    @Override
    default int compareTo(@NotNull final Row row) {
        return getEntity().compareTo(row.getEntity());
    }
}
