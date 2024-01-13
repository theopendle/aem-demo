package com.theopendle.core.workflow.queries;

import lombok.RequiredArgsConstructor;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.Query;
import org.apache.jackrabbit.api.security.user.QueryBuilder;

@RequiredArgsConstructor
public class GroupWithName implements Query {

    private final String userName;

    @Override
    public <T> void build(final QueryBuilder<T> queryBuilder) {
        queryBuilder.setCondition(queryBuilder.nameMatches(userName));
        queryBuilder.setSelector(Group.class);
    }
}
