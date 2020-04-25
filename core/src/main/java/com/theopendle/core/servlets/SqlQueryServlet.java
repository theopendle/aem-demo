package com.theopendle.core.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.*;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=JCR SQL 2 Query Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/query/sql2"
        })
@Slf4j
public class SqlQueryServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 2598426619166789515L;

    private SlingHttpServletResponse response;
    private PrintWriter out;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {

        this.response = response;
        this.out = response.getWriter();

        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());

        // 400 if no query
        final String q = request.getParameter("q");
        if (q == null) {
            writeResult(new Result("No query"), HttpStatus.SC_BAD_REQUEST);
            return;
        }

        try {
            // Execute query and return results
            final QueryManager queryManager = getQueryManager(request);
            final QueryResult result = executeQuery(queryManager, q);
            writeResult(new Result(result), HttpStatus.SC_OK);

        } catch (final InvalidQueryException e) {
            // If query invalid, show feedback to user
            writeResult(new Result(e.getMessage()), HttpStatus.SC_BAD_REQUEST);
            log.warn("Could not execute query: {}", e.getMessage());

        } catch (final Exception e) {
            // If error is unexpected, log it and inform user without feedback
            writeResult("Could not execute query. See log for details.");
            log.error("Unexpected error", e);
        }
    }

    private void writeResult(final Result result, final int httpStatus) {
        try {
            out.println(new ObjectMapper().writeValueAsString(result));
            response.setStatus(httpStatus);
        } catch (final JsonProcessingException e) {
            writeResult("Could not serialize result");
        }
    }

    private void writeResult(final String string) {
        out.println("{\"error\":  \"" + string + "\"}");
        response.setStatus(500);
    }

    private QueryManager getQueryManager(final SlingHttpServletRequest request) throws RepositoryException {
        return request.getResourceResolver().adaptTo(Session.class).getWorkspace().getQueryManager();
    }

    private QueryResult executeQuery(final QueryManager queryManager, final String queryString) throws RepositoryException {
        final Query query = queryManager.createQuery(queryString, "JCR-SQL2");
        // query.setLimit(4);
        return query.execute();
    }

    @Data
    public class Result {
        private String feedback;
        private List<String> headers;
        private List<ValueList> rows;

        /**
         * Succes constructor: builds the result from data.
         *
         * @param queryResult result of an successfully execute query
         * @throws RepositoryException if JCR values cannot be read
         */
        public Result(final QueryResult queryResult) throws RepositoryException {
            this.headers = Arrays.asList(queryResult.getColumnNames());

            this.rows = new ArrayList<>();
            final RowIterator rows = queryResult.getRows();

            while (rows.hasNext()) {
                final Row row = rows.nextRow();
                final List<String> values = new ArrayList<>();

                for (final String header : headers) {
                    final javax.jcr.Value value = row.getValue(header);
                    values.add(value != null ? value.getString() : "null");
                }
                final ValueList valueList = new ValueList();
                valueList.setValues(values);
                this.rows.add(valueList);
            }
        }

        /**
         * Failure constructor: builds the result from a feedback message about the query error.
         *
         * @param feedback error message of a failed query
         */
        public Result(final String feedback) {
            this.feedback = feedback;
        }
    }

    @Data
    public class ValueList {
        List<String> values;
    }
}
