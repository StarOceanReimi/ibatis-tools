package me.liqiu.mybatisgeneratetools.transformer;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.google.common.collect.Streams;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import me.liqiu.mybatisgeneratetools.guice.InjectProperty;
import me.liqiu.mybatisgeneratetools.guice.PropertiesPrefix;
import me.liqiu.mybatisgeneratetools.model.TableModel;
import me.liqiu.mybatisgeneratetools.rules.NamingRule;
import org.apache.commons.beanutils.converters.ArrayConverter;
import org.apache.commons.beanutils.converters.StringConverter;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

import static org.apache.commons.lang3.SystemUtils.USER_DIR;

@PropertiesPrefix("scripts")
public class ScriptTransformerHelper {

    @InjectProperty(value = "locations", defaultValue = "")
    private String locations;

    @Inject
    private ScriptEngine javascriptEngine;

    @InjectProperty("moduleNameRuleFunc")
    private String moduleNameRuleFunc;

    @InjectProperty("propertyNameRuleFunc")
    private String propertyNameRuleFunc;

    @InjectProperty("filterRuleFunc")
    private String filterRuleFunc;

    @InjectProperty("viewNameMapName")
    private String viewNameMapName;

    private NamingRule moduleNamingRule;
    private NamingRule propertyNamingRule;
    private TableTransformFilter tableFilter;

    private static final Splitter commaSplitter = Splitter.on(',').trimResults();

    @PostConstruct
    public void init() {

        val scriptLocations = commaSplitter.split(locations);

        Streams.stream(scriptLocations)
                .map(path -> {
                    try {
                        return new FileReader(Paths.get(USER_DIR, path).toFile());
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(reader -> {
                    try {
                        javascriptEngine.eval(reader);
                    } catch (ScriptException e) {
                        throw new RuntimeException(e);
                    }
                });

        moduleNamingRule = new NamingRule() {
            @Override
            public String rename(String name) {
                return moduleNameRule(name);
            }
        };

        propertyNamingRule = new NamingRule() {
            @Override
            public String rename(String name) {
                return propertyNameRule(name);
            }
        };

        tableFilter = new TableTransformFilter() {
            @Override
            public boolean filter(TableModel model) {
                return (boolean) tableFilter(model);
            }
        };
    }

    public NamingRule getModuleNamingRule() {
        return moduleNamingRule;
    }

    public NamingRule getPropertyNamingRule() {
        return propertyNamingRule;
    }

    public TableTransformFilter getTableFilter() {
        return tableFilter;
    }

    public boolean tableFilter(TableModel model) {
        return (boolean) invokeFunction(filterRuleFunc, model);
    }

    public String moduleNameRule(String name) {
        return (String) invokeFunction(moduleNameRuleFunc, name);
    }

    public String propertyNameRule(String name) {
        return (String) invokeFunction(propertyNameRuleFunc, name);
    }

    public Object invokeFunction(String name, Object... args) {
        try {
            return ((Invocable) javascriptEngine).invokeFunction(name, args);
        }catch (ScriptException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getViewNameMap() {
        return (Map<String, String>) javascriptEngine.get(viewNameMapName);
    }
}
