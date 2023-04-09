package com.theopendle.core.genericentities2.table;

import com.theopendle.core.genericentities2.AbstractEntity;

public abstract class AbstractRow<T extends AbstractEntity> implements Row {

    protected T entity;

    public AbstractRow(final T entity) {
        this.entity = entity;
    }
}
