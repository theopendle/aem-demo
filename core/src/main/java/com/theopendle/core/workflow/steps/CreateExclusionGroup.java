package com.theopendle.core.workflow.steps;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.theopendle.core.workflow.WorkflowUtil;
import com.theopendle.core.workflow.queries.UsersOfGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.principal.PrincipalImpl;
import org.apache.jackrabbit.value.StringValue;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import java.util.*;
import java.util.stream.Collectors;

import static com.theopendle.core.workflow.WorkflowUtil.setWorkflowVariable;

@Slf4j
@Component(property = {
        "process.label" + "=Create exclusion group",
        Constants.SERVICE_DESCRIPTION + "=Workflow step to create exclusion groups created earlier in the workflow",
        Constants.SERVICE_VENDOR + "=Theo Pendle",
})
public class CreateExclusionGroup implements WorkflowProcess {

    public static final String USER_ID_ADMIN = "admin";
    public static final String PN_EXCLUSION_GROUP_ID = "exclusionGroupId";
    public static final String PN_GROUPS = "groups";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(final WorkItem workItem, final WorkflowSession workflowSession, final MetaDataMap metaDataMap) throws WorkflowException {
        final String initiatorId = workItem.getWorkflow().getInitiator();

        // In case the initiator is the admin user, allow them to self-approve
        if (initiatorId.equals(USER_ID_ADMIN)) {
            log.warn("Initiator is admin. No exclusion group will be created.");
            return;
        }

        final Map<String, String> arguments = WorkflowUtil.readArguments(metaDataMap);
        if (!arguments.containsKey(PN_GROUPS)) {
            throw new WorkflowException(String.format("No <%s> argument passed to step", PN_GROUPS));
        }

        final Set<String> groups = Arrays.stream(arguments.get(PN_GROUPS).split(","))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        if (groups.isEmpty()) {
            throw new WorkflowException(String.format("<%s> argument contains an empty list", PN_GROUPS));
        }

        try {

            try (final ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(Map.of(
                    ResourceResolverFactory.SUBSERVICE, "user-management"))) {

                final UserManager userManager = resolver.adaptTo(UserManager.class);
                if (userManager == null) {
                    throw new WorkflowException(String.format("Could not retrieve <%s>", UserManager.class));
                }

                final Authorizable initiatorAuthorizable = userManager.getAuthorizable(initiatorId);
                if (initiatorAuthorizable == null) {
                    throw new WorkflowException(String.format("Could not find initiator of the workflow with ID <%s> ", initiatorId));
                }

                // Find all users belonging to specified groups
                final Set<Authorizable> users = groups.stream()
                        .flatMap(groupId -> getUsersOfGroup(userManager, groupId).stream())
                        .filter(user -> !user.equals(initiatorAuthorizable))
                        .collect(Collectors.toSet());
                if (users.isEmpty()) {
                    throw new WorkflowException(String.format("No other users found in groups <%s> except initiator <%s>", groups, initiatorAuthorizable.getPrincipal().getName()));
                }

                // Create exclusion group
                final String exclusionGroupId = "demo-exclusion-group-" + UUID.randomUUID();
                final PrincipalImpl exclusionPrincipal = new PrincipalImpl(exclusionGroupId);
                final Group exclusionGroup = userManager.createGroup(exclusionPrincipal, "demo/exclusion");

                // Add properties to group so it can easily be found and recognized
                final String userIds = users.stream()
                        .map(user -> {
                            try {
                                return user.getPrincipal().getName();
                            } catch (final RepositoryException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", "));

                for (final Map.Entry<String, String> entry : Map.of(
                        "workflowInstance", workItem.getWorkflow().getId(),
                        "includesGroups", String.join(",", groups),
                        "excludesUser", initiatorAuthorizable.getPrincipal().getName(),
                        "profile/givenName", userIds
                ).entrySet()) {
                    exclusionGroup.setProperty(entry.getKey(), new StringValue(entry.getValue()));
                }

                // Add users to exclusion group
                for (final Authorizable user : users) {
                    exclusionGroup.addMember(user);
                }

                resolver.commit();
                setWorkflowVariable(workItem, PN_EXCLUSION_GROUP_ID, exclusionGroupId);
                log.info("Created exclusion group with ID <{}>", exclusionGroupId);

            } catch (final LoginException e) {
                throw new WorkflowException("Could not log to service user.", e);
            } catch (final RepositoryException e) {
                throw new WorkflowException("Unexpected error while fetching user and/group", e);
            } catch (final PersistenceException e) {
                throw new WorkflowException("Unexpected error while saving exclusion group", e);
            }

        } catch (final Exception e) {
            throw new WorkflowException(String.format("Unexpected error while running <%s>", this.getClass()), e);
        }
    }

    private Set<User> getUsersOfGroup(final UserManager userManager, final String groupName) {
        try {
            final UsersOfGroup usersOfGroup = new UsersOfGroup(groupName);

            final Iterator<Authorizable> iterator = userManager.findAuthorizables(usersOfGroup);

            final Set<User> users = new HashSet<>();
            while (iterator.hasNext()) {
                final Authorizable authorizable = iterator.next();

                if (authorizable.isGroup()) {
                    log.info("Ignoring authorizable <{}>, member of group <{}> as it is not a user",
                            authorizable.getPrincipal().getName(), groupName);
                    continue;
                }

                users.add((User) authorizable);
            }

            return users;

        } catch (final RepositoryException e) {
            log.error("Unexpected error while searching for Authorizables", e);
            return Collections.emptySet();
        }
    }
}
