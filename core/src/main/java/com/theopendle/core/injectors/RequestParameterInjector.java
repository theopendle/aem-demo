package com.theopendle.core.injectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.osgi.service.component.annotations.Component;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component(service = {Injector.class, StaticInjectAnnotationProcessorFactory.class})
public class RequestParameterInjector implements Injector, StaticInjectAnnotationProcessorFactory {

    public static final String NAME = "request-parameter";

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

        final RequestParameter annotation = element.getAnnotation(RequestParameter.class);
        if (annotation == null) {
            return null;
        }

        if (!(adaptable instanceof SlingHttpServletRequest)) {
            log.error("Cannot adapt <{}> to request parameter value", adaptable.getClass().getSimpleName());
            return null;
        }

        final SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;

        final String fieldName = StringUtils.isNotBlank(annotation.name())
                ? annotation.name()
                : name;

        if (type instanceof ParameterizedType) {
            final Type rawType = ((ParameterizedType) type).getRawType();

            if (!rawType.equals(List.class)) {
                log.error("Request parameter field <{}> cannot be injected as type <{}>", fieldName, type);
                return null;
            }

            final Type componentType = ((ParameterizedType) type).getActualTypeArguments()[0];
            return Arrays.asList(convertArray(componentType, request.getParameterMap().get(fieldName)));
        }

        final String parameter = request.getParameter(fieldName);

        return convert(type, parameter);
    }

    private static Object convert(final Type targetType, final String string) {
        if (string == null) {
            return null;
        }

        if (targetType.equals(String.class)) {
            return string;
        }

        final PropertyEditor editor = PropertyEditorManager.findEditor((Class<?>) targetType);
        if (editor == null) {
            log.error("Request parameter value <{}> cannot be injected as type <{}>", string, targetType);
            return null;
        }

        editor.setAsText(string);
        return editor.getValue();
    }

    private static Object[] convertArray(final Type arrayTargetType, final String[] stringArray) {
        return Arrays.stream(stringArray)
                .map(parameter -> convert(arrayTargetType, parameter))
                .toArray();
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final AnnotatedElement element) {
        final RequestParameter annotation = element.getAnnotation(RequestParameter.class);
        if (annotation != null) {
            return new RequestParameterProcessor(annotation);
        }
        return null;
    }
}
