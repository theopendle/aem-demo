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

    public static void setWorkflowVariable(final WorkItem workItem, final String name, final Object value) {
        workItem.getWorkflowData().getMetaDataMap().put(name, value);
    }

    public static <T> T getWorkflowVariable(final WorkItem workItem, final String name, final Class<T> type) {
        return workItem.getWorkflowData().getMetaDataMap().get(name, type);
    }
}
