package com.theopendle.core.workflow;

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

@Slf4j
@Component(property = {
        "process.label" + "=Clean up exclusion groups",
        Constants.SERVICE_DESCRIPTION + "=Workflow step to clean up exclusion groups created earlier in the workflow",
        Constants.SERVICE_VENDOR + "=Theo Pendle",
})
public class CleanUpExclusionGroups implements WorkflowProcess {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(final WorkItem workItem, final WorkflowSession workflowSession, final MetaDataMap metaDataMap) throws WorkflowException {
        final String exclusionGroupId = workItem.getWorkflowData().getMetaDataMap().get(CreateExclusionGroup.PN_EXCLUSION_GROUP_ID, String.class);

        if (exclusionGroupId == null) {
            log.error("No exclusion group found in workflow metadata map via property <{}>",
                    CreateExclusionGroup.PN_EXCLUSION_GROUP_ID);
            return;
        }

        try (final ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(Map.of(
                ResourceResolverFactory.SUBSERVICE, "user-management"))) {

            final UserManager userManager = resolver.adaptTo(UserManager.class);
            if (userManager == null) {
                log.error("Could not retrieve <{}>", UserManager.class);
                return;
            }

            final Authorizable exclusionGroup = userManager.getAuthorizable(exclusionGroupId);
            if (exclusionGroup == null) {
                log.error("Could not find exclusion group with ID <{}>", exclusionGroupId);
                return;
            }

            exclusionGroup.remove();
            resolver.commit();
            log.debug("Deleted exclusion group <{}>", exclusionGroupId);

        } catch (final LoginException e) {
            log.error("Could not log to service user.", e);
        } catch (final RepositoryException e) {
            log.error("Unexpected error while fetching user and/group", e);
        } catch (final PersistenceException e) {
            log.error("Unexpected error while saving exclusion group", e);
        }
    }
}
