package com.theopendle.core.injectors.request;

import com.theopendle.core.injectors.AbstractStringSourceInjector;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.osgi.service.component.annotations.Component;

import java.util.Arrays;
import java.util.List;

@Component(service = {Injector.class, StaticInjectAnnotationProcessorFactory.class})
public class RequestParameterInjector
        extends AbstractStringSourceInjector<RequestParameter, SlingHttpServletRequest, RequestParameterProcessor>
        implements Injector, StaticInjectAnnotationProcessorFactory {

    public static final String NAME = "request-parameter";

    public RequestParameterInjector() {
        super(RequestParameter.class, SlingHttpServletRequest.class, RequestParameterProcessor.class);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected String getSourceValue(final SlingHttpServletRequest request, final String sourceName) {
        return request.getParameter(sourceName);
    }

    @Override
    protected List<String> getSourceValues(final SlingHttpServletRequest request, final String sourceName) {
        return Arrays.asList(request.getParameterMap().get(sourceName));
    }
}
