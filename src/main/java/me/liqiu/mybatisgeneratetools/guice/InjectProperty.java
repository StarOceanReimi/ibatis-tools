package me.liqiu.mybatisgeneratetools.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface InjectProperty {

    String value();

    /**
     * if value is not in properties file
     * then use this defaultValue
     * @return
     */
    String defaultValue() default "";
}
