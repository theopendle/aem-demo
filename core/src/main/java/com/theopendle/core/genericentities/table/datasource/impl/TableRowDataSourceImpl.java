//package com.theopendle.core.genericentities.table.datasource.impl;
//
//import com.theopendle.core.genericentities.table.datasource.AbstractTableDataSource;
//import com.theopendle.core.genericentities.table.datasource.TableRowDataSource;
//import com.theopendle.core.genericentities.table.impl.TableRowImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.sling.api.SlingHttpServletRequest;
//import org.apache.sling.models.annotations.DefaultInjectionStrategy;
//import org.apache.sling.models.annotations.Model;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Model(
//        adaptables = SlingHttpServletRequest.class,
//        adapters = TableRowDataSource.class,
//        resourceType = TableRowDataSourceImpl.RESOURCE_TYPE,
//        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
//)
//public class TableRowDataSourceImpl extends AbstractTableDataSource implements TableRowDataSource {
//    public static final String RESOURCE_TYPE = "demo/consoles/genericentity/datasources/tableRowDataSource";
//
//    @PostConstruct
//    public void init() {
//        final List<TableRowImpl> rows = getEntityResourcesAsJsonNodes()
//                .stream()
//                .map(AbstractTableDataSource::getValidValues)
//                .map(TableRowImpl::fromValueNodeMap)
//                .collect(Collectors.toList());
//
//        injectDataIntoRequest(rows);
//    }
//}
