package com.theopendle.core.workflow.steps;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;

import static com.theopendle.core.workflow.WorkflowUtil.getWorkflowVariable;

@Slf4j
@Component(service = ParticipantStepChooser.class, property = {
        ParticipantStepChooser.SERVICE_PROPERTY_LABEL + "=Exclusion group"
})
public class ExclusionGroupParticipantStepChooser implements ParticipantStepChooser {

    @Override
    public String getParticipant(final WorkItem workItem, final WorkflowSession workflowSession, final MetaDataMap metaDataMap) throws WorkflowException {
        final String exclusionGroupId = getWorkflowVariable(workItem, CreateExclusionGroup.PN_EXCLUSION_GROUP_ID, String.class);

        if (exclusionGroupId == null) {
            throw new WorkflowException(String.format("No exclusion group found in workflow metadata map via property <%s>",
                    CreateExclusionGroup.PN_EXCLUSION_GROUP_ID));
        }

        return exclusionGroupId;
    }
}
