package com.theopendle.core.models;

import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.day.cq.commons.jcr.JcrConstants;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = Page.class,
        resourceType = PageImpl.RESOURCE_TYPE
)
@Slf4j
public class PageImpl extends AbstractComponentImpl implements Page {
    public static final String RESOURCE_TYPE = "demo/componnents/page";
    private static final String PN_PUBLICATION_DATE = "publicationDate";

    @Delegate
    @Self
    @Via(type = ResourceSuperType.class)
    private com.adobe.cq.wcm.core.components.models.Page delegate;

    @ValueMapValue
    private LocalDate publicationDate;

    @Getter
    @ValueMapValue
    private Integer readTimeInMinutes;

    @Override
    public String getPublicationDate() {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        // Return the author-set publication date if present
        if (publicationDate != null) {
            return publicationDate.format(dateTimeFormatter);
        }

        log.info("Could not find {} property for page at {}. Reverting to {}",
                PN_PUBLICATION_DATE, resource.getPath(), JcrConstants.JCR_CREATED);

        // Else, return created date if present
        final LocalDate createdDate = resource.getValueMap().get(JcrConstants.JCR_CREATED, LocalDate.class);
        if (createdDate != null) {
            return createdDate.format(dateTimeFormatter);
        }

        // Else, return null
        log.error("Could not find {} on page at {}. Returning null {}",
                JcrConstants.JCR_CREATED, resource.getPath(), PN_PUBLICATION_DATE);

        return null;
    }
}
