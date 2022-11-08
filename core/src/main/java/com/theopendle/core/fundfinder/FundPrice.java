package com.theopendle.core.fundfinder;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class FundPrice {

    @CsvBindByName(column = "isin")
    private String isin;

    @CsvBindByName(column = "price")
    private String price;
}
