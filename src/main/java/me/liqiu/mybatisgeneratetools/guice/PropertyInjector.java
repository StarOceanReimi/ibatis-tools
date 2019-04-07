package me.liqiu.mybatisgeneratetools.guice;

import com.google.common.base.Strings;
import com.google.inject.MembersInjector;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Properties;

public abstract class PropertyInjector<T> implements MembersInjector<T> {

    private String prefix;
    private Properties properties;
    private String name;
    private String defaultValue;

    public PropertyInjector(Properties properties, PropertiesPrefix propertiesPrefix, InjectProperty injectProperty) {
        Objects.requireNonNull(properties);
        Objects.requireNonNull(injectProperty);
        this.properties = properties;
        if(propertiesPrefix != null)
            this.prefix = propertiesPrefix.value();
        else
            this.prefix = "";
        this.name = injectProperty.value();
        this.defaultValue = injectProperty.defaultValue();
    }

    protected abstract void doInject(T instance, String value);

    @Override
    public void injectMembers(T instance) {
        if(!Strings.isNullOrEmpty(prefix) && !prefix.endsWith(".")) prefix += ".";
        String value = properties.getProperty(this.prefix + this.name);
        if(Strings.isNullOrEmpty(value))
            value = defaultValue;
        doInject(instance, value);
    }
}
