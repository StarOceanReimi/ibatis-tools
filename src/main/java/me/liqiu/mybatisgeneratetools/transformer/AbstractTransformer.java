package me.liqiu.mybatisgeneratetools.transformer;

import com.google.inject.Inject;
import me.liqiu.mybatisgeneratetools.guice.InjectProperty;
import me.liqiu.mybatisgeneratetools.model.TableModel;
import me.liqiu.mybatisgeneratetools.rules.NamingRule;

import javax.annotation.PostConstruct;

import static me.liqiu.mybatisgeneratetools.util.ConstructBeanUtil.constructObjectWithCommaListArgs;

public abstract class AbstractTransformer<R> implements Transformer<R> {

    @InjectProperty("packageName")
    protected String packageName;

    @InjectProperty(
        value = "moduleNameRule.class",
        defaultValue = "me.liqiu.mybatisgeneratetools.rules.CapitalizeRegexReplacementRule"
    )
    protected String moduleNameRuleClass;

    @InjectProperty(
        value = "moduleNameRule.args",
        defaultValue = "'^t_',''"
    )
    protected String moduleNameRuleArgs;

    @InjectProperty(
        value = "propertyNameRule.class",
        defaultValue = "me.liqiu.mybatisgeneratetools.rules.UnderlineToCamelRule"
    )
    protected String propertyNameRuleClass;

    @InjectProperty(value = "propertyNameRule.args")
    protected String propertyNameRuleArgs;


    @InjectProperty(
        value = "transformFilter.class",
        defaultValue = "me.liqiu.mybatisgeneratetools.transformer.AbstractTransformer$FilterNothingTransformFilter"
    )
    protected String transformerFilterClass;

    @InjectProperty(value = "transformFilter.args")
    protected String transformerFilterArgs;

    protected NamingRule modelNameRule;

    protected NamingRule propertyNameRule;

    protected TableTransformFilter filter;


    @Inject
    protected ScriptTransformerHelper helper;

    @PostConstruct
    public void buildTransformerByProperties() {
        if(helper != null) {
            modelNameRule = helper.getModuleNamingRule();
            propertyNameRule = helper.getPropertyNamingRule();
            filter = helper.getTableFilter();
        } else {
            modelNameRule = constructObjectWithCommaListArgs(moduleNameRuleClass, moduleNameRuleArgs);
            propertyNameRule = constructObjectWithCommaListArgs(propertyNameRuleClass, propertyNameRuleArgs);
            filter = constructObjectWithCommaListArgs(transformerFilterClass, transformerFilterArgs);
        }
    }

    @Override
    public boolean test(TableModel model) {
        return filter.filter(model);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public NamingRule getModelNameRule() {
        return modelNameRule;
    }

    public void setModelNameRule(NamingRule modelNameRule) {
        this.modelNameRule = modelNameRule;
    }

    public NamingRule getPropertyNameRule() {
        return propertyNameRule;
    }

    public TableTransformFilter getFilter() {
        return filter;
    }

    public void setFilter(TableTransformFilter filter) {
        this.filter = filter;
    }

    public void setPropertyNameRule(NamingRule propertyNameRule) {
        this.propertyNameRule = propertyNameRule;
    }

    public static class FilterNothingTransformFilter implements TableTransformFilter {
        @Override
        public boolean filter(TableModel model) {
            return true;
        }
    }
}
