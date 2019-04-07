package mybatisgeneratetools;

import me.liqiu.mybatisgeneratetools.rules.CapitalizeRegexReplacementRule;
import me.liqiu.mybatisgeneratetools.util.ConstructBeanUtil;
import org.apache.commons.beanutils.converters.ArrayConverter;
import org.apache.commons.beanutils.converters.StringConverter;
import org.junit.Test;

import java.util.Arrays;

public class ApplicationTest {

    @Test
    public void test() {
        CapitalizeRegexReplacementRule rule = ConstructBeanUtil
                .constructObjectWithCommaListArgs(
                        "me.liqiu.mybatisgeneratetools.rules.CapitalizeRegexReplacementRule",
                        "'^t_',''");
        System.out.println(rule.rename("t_name"));


    }

}
