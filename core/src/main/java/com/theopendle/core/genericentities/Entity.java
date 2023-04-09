package com.theopendle.core.genericentities;

import com.adobe.cq.export.json.ComponentExporter;
import com.theopendle.core.genericentities2.table.Row;
import org.apache.sling.api.resource.Resource;

public interface Entity extends ComponentExporter {

    Resource getResource();

    String getPath();

    String getName();

    String getTableResourceType();

    <T extends Row> T getRow();
}
