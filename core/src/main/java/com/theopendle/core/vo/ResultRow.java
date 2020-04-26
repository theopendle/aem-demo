package com.theopendle.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResultRow {
    private List<String> cells;
}
