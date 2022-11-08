package com.theopendle.core.fundfinder;

public interface FundRepository {
    boolean insertOne(FundInfo fundInfo);

    boolean updateOne(FundPrice fundPrice);
}
