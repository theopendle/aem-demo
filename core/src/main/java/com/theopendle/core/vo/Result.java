package com.theopendle.core.vo;

import lombok.Data;

@Data
public class Result {
    private String feedback;
    private double executionTime;
    private ResultTable table;

    /**
     * Success constructor: builds the result from data.
     */
    public Result(final ResultTable table, final double executionTime) {
        this.table = table;
        this.executionTime = executionTime;
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
