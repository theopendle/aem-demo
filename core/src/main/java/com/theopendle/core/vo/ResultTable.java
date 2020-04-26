package com.theopendle.core.vo;

import lombok.Data;

import java.util.List;

@Data
public class ResultTable {
    private String feedback;
    private double executionTime;
    private ResultRow header;
    private List<ResultRow> rows;

    public ResultTable(final ResultRow header, final List<ResultRow> rows) {
        this.header = header;
        this.rows = rows;
    }
}
