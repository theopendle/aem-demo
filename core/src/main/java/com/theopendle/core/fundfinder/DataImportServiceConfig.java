package com.theopendle.core.fundfinder;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Fund finder data import configuration")

public @interface DataImportServiceConfig {

    String LABEL_PATH_DESCRIPTION = "The path represents a file on the server hosting this AEM instance";

    String DEFAULT_VALUE_FUND_INFO_CSV = "/tmp/fund_info.csv";

    @AttributeDefinition(
            name = "Path to the fund information CSV",
            description = DataImportServiceConfig.LABEL_PATH_DESCRIPTION,
            type = AttributeType.STRING,
            defaultValue = DEFAULT_VALUE_FUND_INFO_CSV)
    String fundInfoCsv() default DEFAULT_VALUE_FUND_INFO_CSV;

    String DEFAULT_VALUE_FUND_PRICE_CSV = "/tmp/fund_price.csv";

    @AttributeDefinition(
            name = "Path to the fund price CSV",
            description = DataImportServiceConfig.LABEL_PATH_DESCRIPTION,
            type = AttributeType.STRING,
            defaultValue = DEFAULT_VALUE_FUND_PRICE_CSV)
    String fundPriceCsv() default DEFAULT_VALUE_FUND_PRICE_CSV;
}
