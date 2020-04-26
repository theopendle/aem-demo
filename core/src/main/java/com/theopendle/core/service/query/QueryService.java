package com.theopendle.core.service.query;

import com.theopendle.core.vo.ResultTable;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;

public interface QueryService {

    QueryResult executeQuery(final String q, final SlingHttpServletRequest request) throws RepositoryException;

    ResultTable toTable(final QueryResult queryResult, final String groupByColumn) throws RepositoryException;
}
