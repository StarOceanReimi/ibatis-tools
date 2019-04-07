package me.liqiu.mybatisgeneratetools.rules;

import org.apache.commons.lang3.StringUtils;

public class CapitalizeRegexReplacementRule extends RegexReplacementRule {

    public CapitalizeRegexReplacementRule(String pattern, String replacement) {
        super(pattern, replacement);
    }

    @Override
    public String rename(String name) {
        return StringUtils.capitalize(super.rename(name));
    }
}
