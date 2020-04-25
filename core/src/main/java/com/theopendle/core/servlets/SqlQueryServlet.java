package com.theopendle.core.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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

    private final StringBuilder sb = new StringBuilder();
    private PrintWriter out;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {

        out = response.getWriter();
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());

        final String q = request.getParameter("q");
        if (q == null) {
            output("<p>Please provide a query</p>");
            return;
        }

        try {

            final QueryManager queryManager = getQueryManager(request);
            final QueryResult result = executeQuery(queryManager, q);
            output(new ObjectMapper().writeValueAsString(new Result(result)));


        } catch (final Exception e) {
            output("Error during execution: ");
            output(e.getMessage());
            log.error("Error", e);
        }
    }

    private QueryManager getQueryManager(final SlingHttpServletRequest request) throws RepositoryException {
        return request.getResourceResolver().adaptTo(Session.class).getWorkspace().getQueryManager();
    }

    private QueryResult executeQuery(final QueryManager queryManager, final String queryString) throws RepositoryException {
        final Query query = queryManager.createQuery(queryString, "JCR-SQL2");
        // query.setLimit(4);
        return query.execute();
    }

    private void output(final String string) {
        out.println(string);
        sb.append(string);
        sb.append("\n");
    }

    @Data
    public class Result {
        private final List<String> headers;
        private final List<ValueList> rows;

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
    }

    @Data
    public class ValueList {
        List<String> values;
    }
}
