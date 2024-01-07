package com.theopendle.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component(property = {
        "process.label" + "=Create exclusion group",
        Constants.SERVICE_DESCRIPTION + "=Workflow step to create exclusion groups created earlier in the workflow",
        Constants.SERVICE_VENDOR + "=Theo Pendle",
})
public class CreateExclusionGroup implements WorkflowProcess {

    public static final String PN_EXCLUSION_GROUP_ID = "exclusionGroupId";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(final WorkItem workItem, final WorkflowSession workflowSession, final MetaDataMap metaDataMap) throws WorkflowException {
        final String initiatorId = workItem.getWorkflow().getInitiator();

        if (initiatorId.equals("admin")) {
            log.warn("Initiator is admin. No exclusion group will be created.");
            return;
        }

        try {
            final Arguments arguments = readArguments(metaDataMap);

            try (final ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(Map.of(
                    ResourceResolverFactory.SUBSERVICE, "user-management"))) {

                final UserManager userManager = resolver.adaptTo(UserManager.class);
                if (userManager == null) {
                    log.error("Could not retrieve <{}>", UserManager.class);
                    return;
                }

                final Authorizable initiatorAuthorizable = userManager.getAuthorizable(initiatorId);
                if (initiatorAuthorizable == null) {
                    log.error("Could not find initiator of the workflow with ID <{}> ", initiatorId);
                    return;
                }

                final Set<Authorizable> users = arguments.groups.stream()
                        .flatMap(groupName -> getUsersOfGroup(userManager, groupName).stream())
                        .filter(user -> !user.equals(initiatorAuthorizable))
                        .collect(Collectors.toSet());

                if (users.isEmpty()) {
                    log.error("No other users found in groups <{}> except initiator <{}>", arguments.getGroups(), initiatorAuthorizable.getPrincipal().getName());
                    return;
                }

                // Create exclusion group
                final String exclusionGroupId = "demo-exclusion-group-" + UUID.randomUUID();
                final PrincipalImpl exclusionPrincipal = new PrincipalImpl(exclusionGroupId);
                final Group exclusionGroup = userManager.createGroup(exclusionPrincipal, "demo/exclusion");

                // Add properties to group so it can easily be found and recognized
                final String userNameList = users.stream()
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
                        "includesGroups", String.join(",", arguments.getGroups()),
                        "excludesUser", initiatorAuthorizable.getPrincipal().getName(),
                        "profile/givenName", userNameList
                ).entrySet()) {
                    exclusionGroup.setProperty(entry.getKey(), new StringValue(entry.getValue()));
                }

                // Add users to exclusion group
                for (final Authorizable user : users) {
                    exclusionGroup.addMember(user);
                }

                resolver.commit();
                workItem.getWorkflowData().getMetaDataMap().put(PN_EXCLUSION_GROUP_ID, exclusionGroup.getID());
                log.debug("Created exclusion group with ID <{}>", exclusionGroupId);

            } catch (final LoginException e) {
                log.error("Could not log to service user.", e);
            } catch (final RepositoryException e) {
                log.error("Unexpected error while fetching user and/group", e);
            } catch (final PersistenceException e) {
                log.error("Unexpected error while saving exclusion group", e);
            }

        } catch (final Exception e) {
            log.error("Unexpected error while running <{}>", this.getClass(), e);
        }
    }

    private Arguments readArguments(final MetaDataMap metaDataMap) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String argumentsJson = metaDataMap.get("PROCESS_ARGS", String.class);
        return objectMapper.readValue(argumentsJson, Arguments.class);
    }

    private Set<User> getUsersOfGroup(final UserManager userManager, final String groupName) {
        try {
            final UsersOfGroup usersOfGroup = new UsersOfGroup(groupName);

            final Iterator<Authorizable> iterator = userManager.findAuthorizables(usersOfGroup);

            final Set<User> users = new HashSet<>();
            while (iterator.hasNext()) {
                final Authorizable authorizable = iterator.next();

                if (authorizable.isGroup()) {
                    log.debug("Ignoring authorizable <{}>, member of group <{}> as it is not a user",
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

    @Getter
    @NoArgsConstructor
    public static class Arguments {
        private List<String> groups;
    }
}
