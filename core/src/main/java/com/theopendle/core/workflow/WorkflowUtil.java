package com.theopendle.core.workflow;


import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class WorkflowUtil {
    public static final String PROCESS_ARGS_KEY = "PROCESS_ARGS";

    private WorkflowUtil() {
        // noop
    }

    /**
     * Utility method for reading workflow PROCESS_ARGS into a Map.
     *
     * @param args the workflow arguments
     * @return the PROCESS_ARGS args attribute as a map, where the key is the arg name and the value is the arg value
     */
    public static Map<String, String> readArguments(final MetaDataMap args) {
        if (args.containsKey(PROCESS_ARGS_KEY)) {
            return Arrays.stream(args.get(PROCESS_ARGS_KEY, "string")
                            .split(System.lineSeparator()))
                    .filter(StringUtils::isNotBlank)
                    .map(line -> line.split("="))
                    .filter(split -> split.length == 2)
                    .collect(Collectors.toMap(
                            split -> split[0],
                            split -> split[1]
                    ));
        }

        return Collections.emptyMap();
    }

    /**
     * Utility method for setting a workflow variable so it can be retrieved in subsequent steps
     *
     * @param workItem the current work item
     * @param name     the name of the variable
     * @param value    the value of the variable
     */
    public static void setWorkflowVariable(final WorkItem workItem, final String name, final Object value) {
        workItem.getWorkflowData().getMetaDataMap().put(name, value);
    }

    /**
     * Utility method for getting a workflow variable set during a previous step
     *
     * @param workItem the current work item
     * @param name     the name of the variable
     * @param type     the desired type of the varaible
     * @param <T>      the variable type
     * @return the variable value, as an object of the desired type, or null if no such value could be retrieved
     */
    public static <T> T getWorkflowVariable(final WorkItem workItem, final String name, final Class<T> type) {
        return workItem.getWorkflowData().getMetaDataMap().get(name, type);
    }
}
