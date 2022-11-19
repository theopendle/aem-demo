package com.theopendle.core.injection.cookie;

import lombok.AllArgsConstructor;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;

@AllArgsConstructor
public class CookieValueProcessor implements InjectAnnotationProcessor2 {

    private CookieValue annotation;

    @Override
    public String getName() {
        return null;
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
