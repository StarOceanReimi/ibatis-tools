package me.liqiu.mybatisgeneratetools.guice;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;

public class PropertiesFilesTypeListener implements TypeListener {

    Properties properties;

    public PropertiesFilesTypeListener(Properties properties) {
        this.properties = properties;
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        Class<?> clazz = type.getRawType();
        PropertiesPrefix config = clazz.getAnnotation(PropertiesPrefix.class);

        while(clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields) {
                if(field.isAnnotationPresent(InjectProperty.class)) {
                    InjectProperty property = field.getAnnotation(InjectProperty.class);
                    encounter.register(new FieldPropertyInjector<>(
                            field, properties, config, property));
                }
            }
            clazz = clazz.getSuperclass();
        }

    }
}
