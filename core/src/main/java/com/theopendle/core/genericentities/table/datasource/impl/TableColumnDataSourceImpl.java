//package com.theopendle.core.genericentities.table.datasource.impl;
//
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.theopendle.core.genericentities.table.datasource.AbstractTableDataSource;
//import com.theopendle.core.genericentities.table.datasource.TableColumnDataSource;
//import com.theopendle.core.genericentities.table.impl.TableColumnImpl;
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
//        adapters = TableColumnDataSource.class,
//        resourceType = TableColumnDataSourceImpl.RESOURCE_TYPE,
//        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
//)
//public class TableColumnDataSourceImpl extends AbstractTableDataSource implements TableColumnDataSource {
//
//    public static final String RESOURCE_TYPE = "demo/consoles/genericentity/datasources/tableColumnDataSource";
//
//    @PostConstruct
//    public void init() {
//
//        // Set fallback
//        injectDataIntoRequest(List.of(TableColumnImpl.empty()));
//
//
//        final List<ObjectNode> entityObjectNodes = getEntityResourcesAsJsonNodes();
//        if (entityObjectNodes.isEmpty()) {
//            log.warn("No entities exist under <{}>, therefore columns could not be generated dynamically",
//                    entityConfig.getRootResource().getPath());
//            return;
//        }
//
//        final ObjectNode firstEntityObjectNode = entityObjectNodes.get(0);
//        final List<TableColumnImpl> columns = AbstractTableDataSource.getValidValues(firstEntityObjectNode)
//                .entrySet()
//                .stream()
//                .map(entry -> TableColumnImpl.fromValueNodeMap(entry.getKey(), entry.getValue()))
//                .collect(Collectors.toList());
//
//        injectDataIntoRequest(columns);
//    }
//}
