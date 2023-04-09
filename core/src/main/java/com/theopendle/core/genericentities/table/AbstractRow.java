package com.theopendle.core.genericentities.table;

import com.theopendle.core.genericentities.AbstractEntity;

public abstract class AbstractRow<T extends AbstractEntity> implements Row {

    protected T entity;

    public AbstractRow(final T entity) {
        this.entity = entity;
    }
}
