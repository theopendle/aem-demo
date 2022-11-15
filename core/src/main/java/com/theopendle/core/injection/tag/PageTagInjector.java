package com.theopendle.core.injection.tag;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.converter.TypeReference;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Injects the one or many tags applied to page on which the component is placed.
 */
@Slf4j
@Component(service = {Injector.class, StaticInjectAnnotationProcessorFactory.class})
public class PageTagInjector implements Injector, StaticInjectAnnotationProcessorFactory {
    public static final String NAME = "cookie-value";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object getValue(final Object adaptable,
                           final String name,
                           final Type type,
                           final AnnotatedElement element,
                           final DisposalCallbackRegistry callbackRegistry) {

        final PageTag annotation = element.getAnnotation(PageTag.class);
        if (annotation == null) {
            return null;
        }

        final Resource resource = getResourceAdaptable(adaptable);
        if (resource == null) {
            log.error("Could not resolve adaptable <{}> to resource", adaptable);
            return null;
        }

        final ResourceResolver resolver = resource.getResourceResolver();

        final PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            log.error("Could not retrieve page manager from resource <{}>", resource.getPath());
            return null;
        }

        final Page containingPage = pageManager.getContainingPage(resource);
        if (containingPage == null) {
            log.error("Resource <{}> is not part of a page", resource.getPath());
            return null;
        }

        // Get list of tags
        final List<Tag> tags;
        if (StringUtils.isBlank(annotation.name())) {
            tags = Arrays.asList(containingPage.getTags());

        } else {
            final TagManager tagManager = resolver.adaptTo(TagManager.class);
            if (tagManager == null) {
                log.error("Cloud not retrieve tag manager from resource <{}>", resource.getPath());
                return null;
            }

            tags = readCustomTagNames(containingPage, annotation.name())
                    .stream()
                    .map(tagManager::resolve)
                    .filter(tag -> !Objects.isNull(tag))
                    .collect(Collectors.toList());
        }

        // Determine type to return
        if (type.equals(Tag.class)) {
            return tags.isEmpty()
                    ? null
                    : tags.get(0);
        } else if (type.equals(new TypeReference<List<Tag>>() {
        }.getType())) {
            return tags;
        } else {
            log.error("Cannot inject tags into element of type <{}>. Must be Tag or List<Tag>", type);
            return null;
        }
    }

    private static List<String> readCustomTagNames(final Page page, final String propertyName) {
        final ValueMap properties = page.getProperties();
        if (properties == null) {
            log.error("Could not read page properties for page <{}>", page.getPath());
            return new ArrayList<>();
        }

        final String[] tags = properties.get(propertyName, String[].class);
        if (tags == null) {
            log.error("Could not read property <{}> on page <{}>", propertyName, page.getPath());
            return new ArrayList<>();
        }

        return Arrays.asList(tags);
    }

    private static Resource getResourceAdaptable(final Object adaptable) {
        if (adaptable instanceof Resource) {
            return (Resource) adaptable;

        } else if (adaptable instanceof SlingHttpServletRequest) {
            return ((SlingHttpServletRequest) adaptable).getResource();
        } else {
            log.error("Cannot fetch tags from adaptable <{}>. Adaptable must be one of: <{}>", adaptable.getClass(),
                    Arrays.asList(SlingHttpServletRequest.class, Resource.class));
            return null;
        }
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final AnnotatedElement element) {
        // Check if the element has the expected annotation
        final PageTag annotation = element.getAnnotation(PageTag.class);
        if (annotation != null) {
            return new PageTagProcessor(annotation);
        }
        return null;
    }
}
