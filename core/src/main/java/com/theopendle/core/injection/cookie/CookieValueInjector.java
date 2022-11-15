package com.theopendle.core.injection.cookie;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.osgi.service.component.annotations.Component;

import javax.servlet.http.Cookie;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * Injects the value of a cookie derived from a SlingHttpServletRequest.
 */
@Slf4j
@Component(service = {Injector.class, StaticInjectAnnotationProcessorFactory.class})
public class CookieValueInjector implements Injector, StaticInjectAnnotationProcessorFactory {
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

        final CookieValue annotation = element.getAnnotation(CookieValue.class);
        if (annotation == null) {
            return null;
        }

        if (!type.equals(String.class)) {
            log.error("Cookie value can only be injected as <{}>", String.class.getSimpleName());
            return null;
        }

        if (!(adaptable instanceof SlingHttpServletRequest)) {
            log.error("Cannot adapt <{}> to cookie value", adaptable.getClass().getSimpleName());
            return null;
        }

        final SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;

        // Use the cookie name if provided, else use the name of the annotated element (eg: field name)
        final String cookieName = StringUtils.isNotBlank(annotation.cookie())
                ? annotation.cookie()
                : name;

        final Cookie cookie = request.getCookie(cookieName);
        if (cookie == null) {
            log.debug("Could not read value from cookie <{}> as no such cookie exists", cookieName);
            return null;
        }

        return cookie.getValue();
    }

    // This rather clunky method in conjunction with CookieValueProcessor to pass the values of the annotation to the
    // Sling framework. Especially critical for the InjectionStrategy!
    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final AnnotatedElement element) {
        // Check if the element has the expected annotation
        final CookieValue annotation = element.getAnnotation(CookieValue.class);
        if (annotation != null) {
            return new CookieValueProcessor(annotation);
        }
        return null;
    }
}
