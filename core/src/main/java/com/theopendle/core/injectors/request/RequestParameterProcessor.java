package com.theopendle.core.injectors.request;

import com.theopendle.core.injectors.AbstractSimpleInjectAnnotationProcessor;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;

public class RequestParameterProcessor
        extends AbstractSimpleInjectAnnotationProcessor<RequestParameter>
        implements InjectAnnotationProcessor2 {

    public RequestParameterProcessor(final RequestParameter annotation) {
        super(annotation);
    }

    @Override
    public InjectionStrategy getInjectionStrategy() {
        return annotation.injectionStrategy();
    }
}