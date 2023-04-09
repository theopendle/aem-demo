package com.theopendle.core.genericentities;

import com.adobe.cq.export.json.ComponentExporter;
import com.theopendle.core.genericentities.table.Table;
import org.apache.sling.api.resource.Resource;

public interface Entity extends ComponentExporter {

    Resource getResource();

    String getPath();

    String getName();

    String getRowResourceType();

    <T extends Table> T getTable();
}
