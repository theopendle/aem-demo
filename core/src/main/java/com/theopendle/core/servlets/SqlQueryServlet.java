package com.theopendle.core.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theopendle.core.service.query.QueryService;
import com.theopendle.core.vo.Result;
import com.theopendle.core.vo.ResultTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=JCR SQL 2 Query Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/query/sql2"
        })
@Slf4j
public class SqlQueryServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 2598426619166789515L;

    @Reference
    private QueryService service;

    private SlingHttpServletResponse response;
    private PrintWriter out;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        final long startTime = System.nanoTime();

        this.response = response;
        this.out = response.getWriter();

        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());

        // 400 if no query
        final String q = request.getParameter("q");
        if (q == null) {
            writeResult(new Result("No query"), HttpStatus.SC_BAD_REQUEST);
            return;
        }

        final Map.Entry<String, String> extracted = extractGroupBy(q);
        final String sanitizedQuery = extracted.getKey();
        final String groupByColumn = extracted.getValue();

        try {
            // Execute query and return results
            final QueryResult queryResult = service.executeQuery(sanitizedQuery, request);
            final ResultTable resultTable = service.toTable(queryResult, groupByColumn);
            writeResult(new Result(resultTable, System.nanoTime() - startTime), HttpStatus.SC_OK);

        } catch (final InvalidQueryException | UnsupportedOperationException e) {
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

    private Map.Entry<String, String> extractGroupBy(final String q) {
        final Pattern groupByPattern = Pattern.compile("group\\s+by\\s+(\\w*.)?(\\[[\\w:]+\\])",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);


        final List<String> groupByMatchGroups = getMatchGroups(groupByPattern, q);

        if (!groupByMatchGroups.isEmpty()) {
            final String clause = groupByMatchGroups.get(0);
            final String column = groupByMatchGroups.get(2).replaceAll("[\\[\\]]", "");
            String prefix = groupByMatchGroups.get(1);

            if (prefix == null) {
                final Pattern selectPattern = Pattern.compile("select.*from",
                        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
                final List<String> selectMatchGroups = getMatchGroups(selectPattern, q);

                if (selectMatchGroups.get(0).contains("*")) {
                    final Pattern forPattern = Pattern.compile("from\\s+(\\[[\\w:]+\\])",
                            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
                    final List<String> forMatchGroups = getMatchGroups(forPattern, q);
                    final String table = forMatchGroups.get(1);
                    prefix = table.replaceAll("[\\[\\]]", "") + ".";
                } else {
                    prefix = "";
                }
            }

            return new AbstractMap.SimpleEntry<>(q.replace(clause, " "), prefix + column);
        }
        return new AbstractMap.SimpleEntry<>(q, null);
    }

    private List<String> getMatchGroups(final Pattern pattern, final String string) {
        final Matcher matcher = pattern.matcher(string);
        final List<String> groups = new ArrayList<>();
        if (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                groups.add(matcher.group(i));
            }
        }
        return groups;
    }
}
