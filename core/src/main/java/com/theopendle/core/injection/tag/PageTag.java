package com.theopendle.core.injection.tag;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@InjectAnnotation
@Source(PageTagInjector.NAME)
public @interface PageTag {

    /**
     * The name of the property to read tags from.
     */
    String name() default "cq:tags";

    InjectionStrategy injectionStrategy() default InjectionStrategy.OPTIONAL;
}
