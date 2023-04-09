package com.theopendle.core.genericentities.table.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.theopendle.core.genericentities.table.TableRow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Model(adaptables = Resource.class, adapters = TableRow.class)
public class TableRowImpl implements TableRow {

    // TODO: remove this
//    @Getter
//    private List<TableCellImpl> cells;

    @Getter
    @ValueMapValue
    private List<String> values;

    @PostConstruct
    public void init() {
        log.debug("Rendering row with values <{}>", values);
    }

    public static TableRowImpl fromValueNodeMap(final Map<String, ValueNode> valueNodeMap) {
        final List<String> cells = valueNodeMap.values()
                .stream()
                .map(JsonNode::asText) //new TableCellImpl(valueNode.asText(), valueNode.asText()))
                .collect(Collectors.toList());

        return new TableRowImpl(cells);
    }
}
