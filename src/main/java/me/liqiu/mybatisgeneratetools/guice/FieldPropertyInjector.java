package me.liqiu.mybatisgeneratetools.guice;

import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.Field;
import java.util.Properties;

public class FieldPropertyInjector<T> extends PropertyInjector<T> {

    private Field field;

    public FieldPropertyInjector(Field field,
                                 Properties properties,
                                 PropertiesPrefix propertiesPrefix,
                                 InjectProperty injectProperty) {
        super(properties, propertiesPrefix, injectProperty);
        this.field = field;
        this.field.setAccessible(true);
    }

    @Override
    protected void doInject(T instance, String value) {
        try {
            field.set(instance, ConvertUtils.convert(value, field.getType()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
