package me.liqiu.mybatisgeneratetools.guice;

import com.google.inject.spi.InjectionListener;
import org.apache.commons.lang3.reflect.MethodUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class PostConstructInjectionListener implements InjectionListener {

    @Override
    public void afterInjection(Object injectee) {

        Consumer<Method> invoke = m -> {
            try {
                m.invoke(injectee, new Object[0]);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };

        MethodUtils.getMethodsListWithAnnotation(injectee.getClass(), PostConstruct.class)
                .stream()
                .filter(m -> m.getParameterCount() == 0)
                .forEach(invoke);
    }
}
