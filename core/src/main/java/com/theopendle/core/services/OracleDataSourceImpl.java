package com.theopendle.core.services;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component(service = OracleDataSource.class, immediate = true)
public class OracleDataSourceImpl implements OracleDataSource {
    public static final String DATA_SOURCE_NAME = "oracle";

    @Reference
    private DataSourcePool dataSourcePool;

    @Activate
    public void activate() {
        try {
            final DataSource dataSource = (DataSource) dataSourcePool.getDataSource(DATA_SOURCE_NAME);

            try (final Connection connection = dataSource.getConnection()) {
                if (connection == null) {
                    log.error("Could not establish connection to <{}>", DATA_SOURCE_NAME);
                    return;
                }
                log.info("Connection is valid: <{}>", connection.isValid(1000));
            }

        } catch (final SQLException e) {
            log.error("Could not establish connection to <{}>", DATA_SOURCE_NAME, e);

        } catch (final DataSourceNotFoundException e) {
            log.error("Could not find data source with name <{}>", DATA_SOURCE_NAME, e);
        }
    }
}
