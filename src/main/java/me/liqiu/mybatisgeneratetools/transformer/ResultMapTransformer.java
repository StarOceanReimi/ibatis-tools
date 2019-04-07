package me.liqiu.mybatisgeneratetools.transformer;

import me.liqiu.mybatisgeneratetools.guice.PropertiesPrefix;
import me.liqiu.mybatisgeneratetools.model.ResultMapModel;
import me.liqiu.mybatisgeneratetools.model.TableModel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@PropertiesPrefix("transform")
public class ResultMapTransformer extends AbstractTransformer<ResultMapModel> {

    @Override
    public ResultMapModel apply(TableModel model) {
        buildTransformerByProperties();
        ResultMapModel resultMapModel = new ResultMapModel();
        String objectName = Stream.of(model.getTableName())
                .map(modelNameRule::rename)
                .findFirst().get();
        resultMapModel.setObjectName(objectName);
        resultMapModel.setObjectFullName(format("%s.%s", packageName, objectName));
        resultMapModel.setTableColumnNames(model.getColumnNames());
        List<String> propertyNames = model.getColumnNames().stream()
                .map(propertyNameRule::rename)
                .collect(toList());
        resultMapModel.setObjectPropertyNames(propertyNames);
        return resultMapModel;
    }

}
