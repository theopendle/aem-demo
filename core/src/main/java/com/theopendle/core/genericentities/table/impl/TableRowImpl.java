package com.theopendle.core.genericentities.table.impl;

import com.fasterxml.jackson.databind.node.ValueNode;
import com.theopendle.core.genericentities.table.TableRow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Model(adaptables = Resource.class)
public class TableRowImpl implements TableRow {

    @Getter
    private List<TableCellImpl> cells;

    public static TableRowImpl fromValueNodeMap(final Map<String, ValueNode> valueNodeMap) {
        final List<TableCellImpl> cells = valueNodeMap.values()
                .stream()
                .map(valueNode -> new TableCellImpl(valueNode.asText(), valueNode.asText()))
                .collect(Collectors.toList());

        return new TableRowImpl(cells);
    }
}
