package com.theopendle.core.fundfinder;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.metatype.annotations.Designate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiConsumer;

@Slf4j
@Component(service = DataImportService.class, scope = ServiceScope.SINGLETON, immediate = true)
@Designate(ocd = DataImportServiceConfig.class)
public class DataImportServiceImpl implements DataImportService {

    @Reference
    private FundRepository fundRepository;

    private DataImportServiceConfig configuration;

    @Activate
    protected void activate(final DataImportServiceConfig configuration) {
        this.configuration = configuration;
    }

    @Override
    public void importDataFromCsv() {
        try (final Connection connection = null) {
            connection.setAutoCommit(false);

            // Read first CSV and insert
            final ImportMapping fundInfoImportMapping = ImportMapping.builder()
                    .filePath(configuration.fundInfoCsv())
                    .query("insert into aem_apl.fund (isin, name) values (?, ?)")
                    .consumer((preparedStatement, row) -> {
                        try {
                            preparedStatement.setString(1, row[0]);
                            preparedStatement.setString(2, row[1]);
                        } catch (final SQLException e) {
                            log.error("Error mapping of CSV row into <{}>", PreparedStatement.class.getSimpleName(), e);
                        }
                    })
                    .build();

            // Update, using the ISIN as a key
            final ImportMapping fundPriceImportMapping = ImportMapping.builder()
                    .filePath(configuration.fundPriceCsv())
                    .query("update aem_apl.fund set price = ? where isin = ?")
                    .consumer((preparedStatement, row) -> {
                        try {
                            preparedStatement.setString(1, row[1]);
                            preparedStatement.setString(2, row[0]);
                        } catch (final SQLException e) {
                            log.error("Error mapping of CSV row into <{}>", PreparedStatement.class.getSimpleName(), e);
                        }
                    })
                    .build();

            for (final ImportMapping mapping : Arrays.asList(fundInfoImportMapping, fundPriceImportMapping)) {
                if (!importByMapping(connection, mapping)) {
                    log.error("Error during import from <{}>. Abandoning import", mapping.getFilePath());
                    connection.rollback();
                    return;
                }
            }

            connection.commit();

        } catch (final SQLException e) {
            log.error("Could not configure connection", e);
        }
    }

    private boolean importByMapping(final Connection connection, final ImportMapping importMapping) {

        try (final CSVReader reader = new CSVReaderBuilder(new FileReader(configuration.fundInfoCsv())).build()) {

            executeBatch(connection, reader.iterator(), importMapping);

        } catch (final FileNotFoundException e) {
            log.error("Could not find <{}>", configuration.fundInfoCsv(), e);
            return false;
        } catch (final IOException e) {
            log.error("Could not read <{}> as CSV", configuration.fundInfoCsv(), e);
            return false;
        } catch (final SQLException e) {
            log.error("Error during import of <{}>", configuration.fundInfoCsv(), e);
        }

        return true;
    }

    private static void executeBatch(final Connection connection, final Iterator<String[]> rows, final ImportMapping importMapping) throws SQLException {

        try (final PreparedStatement preparedStatement = connection.prepareStatement(importMapping.getQuery())) {

            int rowCount = 0;
            while (rows.hasNext()) {
                final String[] row = rows.next();
                importMapping.getConsumer().accept(preparedStatement, row);
                preparedStatement.addBatch();
                rowCount++;
            }

            final int[] result = preparedStatement.executeBatch();
            log.info("Inserted <{}> rows: <{}>", rowCount, result);
        }
    }

    @Getter
    @Builder
    private static class ImportMapping {
        /**
         * The path to the CSV file source
         */
        private String filePath;

        /**
         * The prepared statement query with which to insert/update a row in the DB
         */
        private String query;

        /**
         * A consumer which maps the contents of a line of CSV (String[]) onto the values of the query (PreparedStatement)
         */
        private BiConsumer<PreparedStatement, String[]> consumer;
    }
}
