package me.liqiu.mybatisgeneratetools.rules;

public class RegexReplacementRule implements NamingRule {

    private final String pattern;

    private final String replacement;

    public RegexReplacementRule(String pattern, String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    @Override
    public String rename(String name) {
        return name.replaceAll(pattern, replacement);
    }
}
