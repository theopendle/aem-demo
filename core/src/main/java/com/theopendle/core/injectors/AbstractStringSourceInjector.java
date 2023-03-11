package com.theopendle.core.injectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public abstract class AbstractStringSourceInjector<R extends Annotation, T, P extends InjectAnnotationProcessor2>
        implements Injector, StaticInjectAnnotationProcessorFactory {

    protected Class<R> annotationClass;
    protected Class<T> adaptableClass;
    protected Class<P> processorClass;

    protected abstract String getSourceValue(T adaptable, String sourceName);

    protected abstract List<String> getSourceValues(T adaptable, String sourceName);

    protected String getSourceName(final R annotation, final String name) {
        return name;
    }

    @Override
    public Object getValue(final Object adaptable,
                           final String name,
                           final Type type,
                           final AnnotatedElement element,
                           final DisposalCallbackRegistry callbackRegistry) {

        // Check annotation is as expected
        final R annotation = element.getAnnotation(annotationClass);
        if (annotation == null) {
            return null;
        }

        // Check adaptable is of correct type
        if (!adaptableClass.isAssignableFrom(adaptable.getClass())) {
            log.error("Cannot adapt from <{}>. Please provide an adaptable of type <{}>",
                    adaptable.getClass(), adaptableClass);
            return null;
        }

        final T castAdaptable = adaptableClass.cast(adaptable);
        final String sourceName = getSourceName(annotation, name);

        // Attempt to inject multi-value
        if (type instanceof ParameterizedType) {
            final Type rawType = ((ParameterizedType) type).getRawType();

            if (!rawType.equals(List.class)) {
                log.error("Source parameter <{}> cannot be injected with type <{}>", sourceName, type);
                return null;
            }

            final Type componentType = ((ParameterizedType) type).getActualTypeArguments()[0];
            return convertMultiple(componentType, getSourceValues(castAdaptable, sourceName));
        }

        // Inject single value
        return convertSingle(type, getSourceValue(castAdaptable, sourceName));
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final AnnotatedElement element) {
        final R annotation = element.getAnnotation(annotationClass);
        if (annotation == null) {
            log.error("No annotation of type <{}> found for element <{}>", annotationClass, element);
            return null;
        }

        try {
            return processorClass.getDeclaredConstructor(annotationClass).newInstance(annotation);
        } catch (final NoSuchMethodException | InvocationTargetException | InstantiationException |
                       IllegalAccessException e) {
            log.error("Cannot instantiate a processor of type <{}> for annotation of type <{}>",
                    processorClass, annotation);
        }

        return null;
    }

    private static Object convertSingle(final Type targetType, final String string) {
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

    private static List<Object> convertMultiple(final Type arrayTargetType, final List<String> strings) {
        return strings.stream()
                .map(parameter -> convertSingle(arrayTargetType, parameter))
                .collect(Collectors.toList());
    }

}
