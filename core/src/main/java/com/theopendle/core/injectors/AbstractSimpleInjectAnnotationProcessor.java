package com.theopendle.core.injectors;

import lombok.AllArgsConstructor;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;

import java.lang.annotation.Annotation;

@AllArgsConstructor
public abstract class AbstractSimpleInjectAnnotationProcessor<R extends Annotation> implements InjectAnnotationProcessor2 {

    protected R annotation;

    @Override
    public abstract InjectionStrategy getInjectionStrategy();

    @Override
    public String getName() {
        return null;
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
