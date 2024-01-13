package com.theopendle.core.workflow.steps;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import java.util.Map;

import static com.theopendle.core.workflow.WorkflowUtil.getWorkflowVariable;

@Slf4j
@Component(property = {
        "process.label" + "=Delete exclusion groups",
        Constants.SERVICE_DESCRIPTION + "=Workflow step to delete exclusion groups created earlier in the workflow",
        Constants.SERVICE_VENDOR + "=Theo Pendle",
})
public class DeleteExclusionGroups implements WorkflowProcess {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(final WorkItem workItem, final WorkflowSession workflowSession, final MetaDataMap metaDataMap) throws WorkflowException {
        final String exclusionGroupId = getWorkflowVariable(workItem, CreateExclusionGroup.PN_EXCLUSION_GROUP_ID, String.class);

        if (exclusionGroupId == null) {
            throw new WorkflowException(String.format("No exclusion group found in workflow metadata map via property <%s>",
                    CreateExclusionGroup.PN_EXCLUSION_GROUP_ID));
        }

        try (final ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(Map.of(
                ResourceResolverFactory.SUBSERVICE, "user-management"))) {

            final UserManager userManager = resolver.adaptTo(UserManager.class);
            if (userManager == null) {
                throw new WorkflowException(String.format("Could not retrieve <%s>", UserManager.class));
            }

            final Authorizable exclusionGroup = userManager.getAuthorizable(exclusionGroupId);
            if (exclusionGroup == null) {
                throw new WorkflowException(String.format("Could not find exclusion group with ID <%s>", exclusionGroupId));
            }

            exclusionGroup.remove();
            resolver.commit();
            log.info("Deleted exclusion group <{}>", exclusionGroupId);

        } catch (final LoginException e) {
            throw new WorkflowException("Could not log to service user.", e);
        } catch (final RepositoryException e) {
            throw new WorkflowException("Unexpected error while fetching user and/group", e);
        } catch (final PersistenceException e) {
            throw new WorkflowException("Unexpected error while saving exclusion group", e);
        }
    }
}
