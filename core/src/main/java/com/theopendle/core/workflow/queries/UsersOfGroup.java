package com.theopendle.core.workflow.queries;

import lombok.RequiredArgsConstructor;
import org.apache.jackrabbit.api.security.user.Query;
import org.apache.jackrabbit.api.security.user.QueryBuilder;
import org.apache.jackrabbit.api.security.user.User;

@RequiredArgsConstructor
public class UsersOfGroup implements Query {

    private final String groupName;

    @Override
    public <T> void build(final QueryBuilder<T> queryBuilder) {
        queryBuilder.setScope(groupName, false);
        queryBuilder.setSelector(User.class);
    }
}
