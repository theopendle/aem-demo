package com.theopendle.core.genericentities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.theopendle.core.datasource.resource.ResourceChildrenDataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.export.spi.ModelExporter;
import org.apache.sling.models.factory.ExportException;
import org.apache.sling.models.factory.ModelFactory;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class GenericEntityTable {

    @OSGiService
    private ModelFactory modelFactory;

    @OSGiService
    private ModelExporter modelExporter;

    @Self
    private ResourceChildrenDataSource dataSource;

    @Self
    private Resource resource;

    @Getter
    private String rowResourceType;

    @Getter
    private List<Column> columns;

    @Getter
    private List<Item> rows;

    @PostConstruct
    private void init() {

        final List<ObjectNode> rows = dataSource.getData()
                .stream()
                .map(this::getModelForResource)
                .filter(Objects::nonNull)
                .map(this::exportModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!rows.isEmpty()) {
            columns = getColumns(rows);
        }

    }

    private AbstractGenericEntity getModelForResource(final Resource resource) {
        try {
            final Object model = modelFactory.getModelFromResource(resource);

            // Make sure that we are getting an entity
            if (!(model instanceof AbstractGenericEntity)) {
                log.warn("Resource at at <{}> with type <{}> was adapted to model of type <{}>, not <{}>",
                        resource, resource.getResourceType(), model.getClass(), AbstractGenericEntity.class);
                return null;
            }

            return (AbstractGenericEntity) model;

        } catch (final RuntimeException e) {
            log.warn("Could not find model for resource at <{}> with type <{}>", resource, resource.getResourceType(), e);
            return null;
        }
    }

    private ObjectNode exportModel(final AbstractExportedModel model) {
        try {
            return modelExporter.export(model, ObjectNode.class, Collections.emptyMap());

        } catch (final ExportException e) {
            log.warn("Could not export <{}> for resource at <{}> to <{}>",
                    model.getExportedType(), resource, ObjectNode.class.getSimpleName(), e);
            return null;
        }
    }

    private static List<Column> getColumns(final List<ObjectNode> objectNodes) {
        final List<Column> columns = new ArrayList<>();

        // TODO: Implement checking of all rows against columns
        final ObjectNode objectNode = objectNodes.get(0);

        objectNode.fieldNames().forEachRemaining(name -> {
            final JsonNode jsonNode = objectNode.get(name);
            final JsonNodeType type = jsonNode.getNodeType();

            // TODO: Implement sorting
            final Map<String, String> sortingAttributes = new HashMap<>();

            columns.add(new Column(name, sortingAttributes));
        });
        return columns;
    }

    @Getter
    @AllArgsConstructor
    static
    class Column {
        private String label;
        private Map<String, String> sortingAttributes;
    }
}
