package com.theopendle.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Slf4j
@Component(service = ParticipantStepChooser.class, property = {
        ParticipantStepChooser.SERVICE_PROPERTY_LABEL + "=Exclusion group"
})
public class ExclusionGroupParticipantStepChooser implements ParticipantStepChooser {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public String getParticipant(final WorkItem workItem, final WorkflowSession workflowSession, final MetaDataMap metaDataMap) throws WorkflowException {
        final String exclusionGroupId = workItem.getWorkflowData().getMetaDataMap().get(CreateExclusionGroup.PN_EXCLUSION_GROUP_ID, String.class);
        if (exclusionGroupId == null) {
            log.error("No exclusion group found in workflow metadata map via property <{}>",
                    CreateExclusionGroup.PN_EXCLUSION_GROUP_ID);
            return "";
        }

        return exclusionGroupId;
    }
}
