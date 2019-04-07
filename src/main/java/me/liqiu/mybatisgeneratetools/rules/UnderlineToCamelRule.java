package me.liqiu.mybatisgeneratetools.rules;

import static java.lang.Character.toUpperCase;

public class UnderlineToCamelRule implements NamingRule {

    @Override
    public String rename(String name) {
        char[] chars = name.toCharArray();
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<chars.length; i++) {
            char c = chars[i];
            if(c != '_') builder.append(c);
            else builder.append(toUpperCase(chars[++i]));
        }
        return builder.toString();
    }
}
