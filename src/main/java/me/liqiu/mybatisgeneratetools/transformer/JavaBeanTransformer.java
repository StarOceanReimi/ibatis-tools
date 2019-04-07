package me.liqiu.mybatisgeneratetools.transformer;

import com.google.common.collect.Streams;
import com.google.inject.Inject;
import lombok.val;
import me.liqiu.mybatisgeneratetools.guice.PropertiesPrefix;
import me.liqiu.mybatisgeneratetools.model.ClassField;
import me.liqiu.mybatisgeneratetools.model.JavaBeanModel;
import me.liqiu.mybatisgeneratetools.model.TableModel;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static me.liqiu.mybatisgeneratetools.util.DbUtil.MYSQL_TYPENAME_CLASS_MAPPING;

@PropertiesPrefix("transform")
public class JavaBeanTransformer extends AbstractTransformer<JavaBeanModel> {

    @Override
    public JavaBeanModel apply(TableModel model) {
        JavaBeanModel javaBeanModel = new JavaBeanModel();
        String className = Stream.of(model.getTableName())
                .map(modelNameRule::rename)
                .findFirst().get();

        javaBeanModel.setClassName(className);
        javaBeanModel.setPackageName(packageName);
        javaBeanModel.setTableName(model.getTableName());
        javaBeanModel.setTableColumnNames(model.getColumnNames());

        val columnClasses = model.getColumnTypes().stream()
                .map(MYSQL_TYPENAME_CLASS_MAPPING::get)
                .collect(toList());

        val classFields = Streams.zip(
                model.getColumnNames().stream()
                        .map(propertyNameRule::rename),
                columnClasses.stream()
                        .map(clazz -> clazz == byte[].class ? "byte[]" : clazz.getSimpleName()),
                ClassField::new).collect(toList());

        javaBeanModel.setClassFields(classFields);

        val imports = columnClasses.stream()
                .filter(c -> c.getPackage() != null)
                .filter(c -> !c.getName().startsWith("java.lang"))
                .map(c -> c.getName())
                .collect(toList());

        javaBeanModel.setImportClasses(imports);

        return javaBeanModel;
    }
}
