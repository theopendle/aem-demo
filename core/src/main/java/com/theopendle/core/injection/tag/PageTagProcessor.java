package com.theopendle.core.injection.tag;

import lombok.AllArgsConstructor;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;

@AllArgsConstructor
public class PageTagProcessor implements InjectAnnotationProcessor2 {

    private PageTag annotation;

    @Override
    public String getName() {
        return annotation.name();
    }

    @Override
    public InjectionStrategy getInjectionStrategy() {
        return annotation.injectionStrategy();
    }

    @Override
    public String getVia() {
        return null;
    }

    @Override
    public boolean hasDefault() {
        return false;
    }

    @Override
    public Object getDefault() {
        return null;
    }

    @Override
    public Boolean isOptional() {
        return null;
    }
}
