package com.theopendle.core.service.query.impl;


import com.theopendle.core.service.query.QueryService;
import com.theopendle.core.vo.ResultRow;
import com.theopendle.core.vo.ResultTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component(service = QueryService.class, immediate = true, scope = ServiceScope.SINGLETON)
@Slf4j
public class QueryServiceImpl implements QueryService {

    @Override
    public QueryResult executeQuery(final String q, final SlingHttpServletRequest request) throws RepositoryException {
        final Session session = request.getResourceResolver().adaptTo(Session.class);
        if (session == null) {
            throw new IllegalArgumentException("Could not get session from request");
        }

        final QueryManager queryManager = session.getWorkspace().getQueryManager();
        return queryManager.createQuery(q, "JCR-SQL2").execute();
    }

    @Override
    public ResultTable toTable(final QueryResult queryResult, final String groupByColumn) throws RepositoryException {
        if (groupByColumn != null) {
            final ResultRow header = new ResultRow(Arrays.asList(groupByColumn, "count"));
            final List<ResultRow> rows = StreamSupport.stream(iterable(queryResult.getRows()).spliterator(), false)
                    .collect(Collectors.groupingBy(row -> {
                        try {
                            return row.getValue(groupByColumn).getString();
                        } catch (final RepositoryException e) {
                            return null;
                        }
                    }, Collectors.counting())).entrySet().stream()
                    .map(entry -> new ResultRow(Arrays.asList(entry.getKey(), Long.toString(entry.getValue()))))
                    .collect(Collectors.toList());
            return new ResultTable(header, rows);

        } else {

            final ResultRow header = new ResultRow(Arrays.asList(queryResult.getColumnNames()));
            final List<ResultRow> rows = StreamSupport.stream(iterable(queryResult.getRows()).spliterator(), false)
                    .map(row -> new ResultRow(getValues(row)))
                    .collect(Collectors.toList());
            return new ResultTable(header, rows);
        }
    }

    private List<String> getValues(final Row row) {
        try {
            final List<String> stringValues = new ArrayList<>();
            for (final Value value : row.getValues()) {
                stringValues.add(value != null ? value.getString() : null);
            }
            return stringValues;
        } catch (final RepositoryException e) {
            throw new IllegalArgumentException("Error retrieving data from JCR", e);
        }
    }

    private Iterable<Row> iterable(final RowIterator rowIterator) {
        return () -> new Iterator<Row>() {

            @Override
            public boolean hasNext() {
                return rowIterator.hasNext();
            }

            @Override
            public Row next() {
                return rowIterator.nextRow();
            }
        };
    }

}
