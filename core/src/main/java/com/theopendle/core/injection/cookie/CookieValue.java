package com.theopendle.core.injection.cookie;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// This annotation belongs on a method (eg: getter), field or parameter (eg: constructor parameter)
@Target({ElementType.FIELD, ElementType.PARAMETER})

// Retained at runtime, as opposed to the Lombok @Getter which is discarded for example
@Retention(RetentionPolicy.RUNTIME)

// Declares an annotation as a custom inject annotation.
@InjectAnnotation

// Tell sling to inject using the CookieValueInjector
@Source(CookieValueInjector.NAME)
public @interface CookieValue {

    /**
     * The name of the cooke to read the value from
     */
    String cookie() default "";

    // Default to OPTIONAL injection strategy as we cannot rely on the cookie being present
    InjectionStrategy injectionStrategy() default InjectionStrategy.OPTIONAL;
}
