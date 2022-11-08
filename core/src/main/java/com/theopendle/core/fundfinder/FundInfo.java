package com.theopendle.core.fundfinder;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class FundInfo {

    @CsvBindByName(column = "isin")
    private String isin;

    @CsvBindByName(column = "name")
    private String name;
}
